package deepLearning.evolution;

import deepLearning.NeuralNetwork;
import experiments.ai.AiTrainingBase; // todo remove dependency

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class EvolutionTraining {

    static final int threads = 12;
    static final int copiesOfChildren = 3;
    static final int maxFailedAttemptsToImprove = 7;

    final BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
    final EvolutionTrainingThread.LabTest test;

    double distortProbability = 0.2;
    double distortValue = 0.7;
    TestSettings settings = new TestSettings();

    public EvolutionTraining(NeuralNetwork startPoint, EvolutionTrainingThread.LabTest test) {
        this.test = test;
        this.currentBest = new TestResult(startPoint, -1000000000);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        name = sdf.format(new Date());
    }

    final String name;
    int iteration = 0;

    TestResult currentBest;
    ArrayList<NeuralNetwork> references = new ArrayList<>();
    ArrayList<TestResult> lastResults = new ArrayList<>();
    ArrayList<TestResult> results = new ArrayList<>();
    double[] progress = new double[0];

    int failedAttemptsToImprove = 0;

    synchronized void taskFinished(NeuralNetwork result, double score){
        results.add(new TestResult(result, score));
    }

    synchronized void reportProgress(int number, double progress) {
        this.progress[number] = progress;
    }

    public void startFromScratch() {
        var r = new Random();
        references = new ArrayList<>();
        progress = new double[threads];
        for (int i = 0; i < threads; i++) {
            var network = new NeuralNetwork(currentBest.network);
            network.randomFill(r);
            references.add(network);
        }

        while (true) {
            runIteration();
        }
    }

    public void continueFromSave() {
        var r = new Random();
        references = new ArrayList<>();
        progress = new double[threads];
        for (int i = 0; i < threads; i++) {
            var network = new NeuralNetwork(currentBest.network);
            network.distort(r, distortProbability, distortValue);
            references.add(network);
        }

        while (true) {
            runIteration();
        }
    }

    void runIteration(){
        long startTime = System.currentTimeMillis();

        results = new ArrayList<>();

        settings.seed = new Random().nextInt();

        progress = new double[threads];
        for (int i = 0; i < references.size(); i++) {
            var thread = new Thread(new EvolutionTrainingThread(this, test, references.get(i), settings, i));
            thread.start();
            //threads.add(thread);
        }

        int progressCheckCounter = 0;
        final int ticksBeforeShowProgress = 50;

        while (!isFinished()) {
            progressCheckCounter++;

            if (progressCheckCounter > ticksBeforeShowProgress) {
                progressCheckCounter = 0;
                var sumProgress = 0.0;
                for (int i = 0; i < progress.length; i++) {
                    sumProgress += progress[i];
                }

                sumProgress /= progress.length;
                var roundedProgress = (int) sumProgress;

                System.out.println("Progress: " + roundedProgress + " ");
            }

            try {
                Thread.sleep(100); // todo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            checkConsoleInput();
        }

        // results.add(new TestResult(startPoint, lastScore)); todo?

        results.addAll(lastResults);
        lastResults = new ArrayList<>();


        TestResult best = new TestResult(null, -111111111);

        for (TestResult result : results) {
            if (result.score > best.score) {
                best = result;
            }
        }

        results.sort((a, b) -> ((int) ((b.score - a.score) * 100)));

        System.out.println(results);
        System.out.println(best.score);

        boolean resetProgress = false;
        boolean failedToImprove = false;

        if (best.score <= currentBest.score) {
            failedToImprove = true;
            failedAttemptsToImprove++;
            System.out.println("Couldn't improve results (" + failedAttemptsToImprove + "/" + maxFailedAttemptsToImprove + ")");

            if (failedAttemptsToImprove >= maxFailedAttemptsToImprove) {
                System.out.println("Got to local minima, resetting progress");// todo makes no sense anymore
                failedAttemptsToImprove = 0;
                currentBest = new TestResult(currentBest.network, -1000000000);
                resetProgress = true;
            }
        } else {
            failedAttemptsToImprove = 0;
            currentBest = best;
        }



        NeuralNetwork.saveNetwork(best.network, "./cache/", "backup");
        if (!failedToImprove) {
            NeuralNetwork.saveNetwork(best.network, "./cache/" + name, iteration + "_" + ((int)best.score));
        }


        var r = new Random();
        settings.seed = r.nextInt();

        references = new ArrayList<>();



        if (resetProgress) {
            for (int i = 0; i < threads; i++) {
                var network = new NeuralNetwork(results.get(i / copiesOfChildren).network);
                network.distort(r, 0.4, 0.7);
                references.add(network);
            }

            lastResults = new ArrayList<>();
        } else {
            // var differenceMap = new NeuralNetworkDifference(new NeuralNetwork[]{
            //         results.get(0).network, results.get(1).network, results.get(2).network
            // });

            for (int i = 0; i < threads; i++) {
                var network = new NeuralNetwork(results.get(i / copiesOfChildren).network);
                network.distort(r, distortProbability, distortValue);
                references.add(network);
            }

            lastResults.addAll(results.subList(0, Math.min(10, results.size())));
        }


        var timeSpent = (System.currentTimeMillis() - startTime) / 1000f;
        System.out.println("Iteration took " + timeSpent + " seconds");

        iteration++;
    }

    // network.distort(r, differenceMap, 0.3, distortProbability, distortValue); todo tryout

    synchronized boolean isFinished(){
        return references.size() == results.size();
    }

    void checkConsoleInput() {
        try {
            if (sc.ready()) {
                System.out.println("Mutation - " + settings.mutationValue);
                System.out.println("Duration - " + settings.duration);
                System.out.println("Tests - " + settings.tests);
                System.out.println("Seed - " + settings.seed);
                System.out.println("Program paused: type the command");
                System.out.println();

                sc.readLine();
                var line = sc.readLine();
                String[] data = line.split(" ");
                String command = data[0].toLowerCase(Locale.ROOT);

                switch (command) {
                    case "mut":
                        settings.mutationValue = Double.parseDouble(data[1]);
                        System.out.println("Mutation set - " + settings.mutationValue);
                        break;
                    case "demo":
                        if (test instanceof AiTrainingBase) {
                            ((AiTrainingBase) test).runDemo(currentBest.network, 0.05);
                        } else {
                            System.out.println("Unsupported");
                        }
                        break;
                    case "save":
                        NeuralNetwork.saveNetwork(currentBest.network, "saved", data[1]);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class TestResult {
        final NeuralNetwork network;
        final double score;

        public TestResult(NeuralNetwork network, double score) {
            this.network = network;
            this.score = score;
        }

        @Override
        public String toString() {
            return "" + ((int) score);
        }
    }

    public static class TestSettings {
        public int failedAttemptsBeforeSeedReset = 200; //300
        public int maxAttempts = 300; // 600

        public int seed = 0;
        public int tests = 25;
        public int duration = 2000;
        public double timeframe = 0.2;

        public double mutationValue = 0.2;
    }
}


