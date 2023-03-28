package deepLearning.backpropagation;


import deepLearning.NeuralNetwork;
import deepLearning.NeuralNetworkDifference;
import deepLearning.NeuralNetworkTrainingData;
import deepLearning.evolution.EvolutionTrainingThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class BackpropagationTraining {

    static final int threads = 5;
    static final int copiesOfChildren = 2;
    static final int maxFailedAttemptsToImprove = 1;

    final BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
    //final EvolutionTrainingThread.LabTest test;

    boolean singleSurvivor = true;
    double distortProbability = 0.01;
    double distortValue = 0.01;

    final NeuralNetworkTrainingData data;

    public BackpropagationTraining(NeuralNetwork startPoint, EvolutionTrainingThread.LabTest test, NeuralNetworkTrainingData data) {
        //this.test = test;
        this.mainReference = new TestResult(startPoint, -1000000000);
        this.data = data;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        name = sdf.format(new Date());
    }

    final String name;
    int iteration = 0;

    TestResult mainReference;
    ArrayList<NeuralNetwork> references = new ArrayList<>();
    ArrayList<TestResult> lastResults = new ArrayList<>();
    ArrayList<TestResult> results = new ArrayList<>();
    double[] progress = new double[0];

    int failedAttemptsToImprove = 0;

    synchronized void taskFinished(NeuralNetwork result, double score) {
        results.add(new TestResult(result, score));
    }

    synchronized void reportProgress(int number, double progress) {
        this.progress[number] = progress;
    }

    public void startFromScratch() {
        System.out.println("Starting from scratch");
        var r = new Random();
        references = new ArrayList<>();
        progress = new double[threads];
        for (int i = 0; i < threads; i++) {
            var network = new NeuralNetwork(mainReference.network);
            network.randomFill(r);
            references.add(network);
        }

        while (true) {
            runIteration();
        }
    }

    public void continueFromSave() {
        System.out.println("Continuing training");
        var r = new Random();
        references = new ArrayList<>();
        progress = new double[threads];
        for (int i = 0; i < threads; i++) {
            var network = new NeuralNetwork(mainReference.network);
            network.distort(r, distortProbability, distortValue);
            references.add(network);
        }

        while (true) {
            runIteration();
        }
    }

    void runIteration() {
        //List<Thread> threads = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        var previousIterationResults = results;
        results = new ArrayList<>();

        progress = new double[threads];
        for (int i = 0; i < references.size(); i++) {
            var fraction = data.getFraction(0.1);
            var thread = new Thread(new BackpropagationThread(this, data, fraction, references.get(i), 2, 1000, i));
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


        TestResult best = new TestResult(null, -1e10);

        for (TestResult result : results) {
            if (result.score > best.score) {
                best = result;
            }
        }

        if (!singleSurvivor) {
            if (previousIterationResults.size() > 2) {
                results.add(previousIterationResults.get(0));
                results.add(previousIterationResults.get(1));
            }
        }

        //results.add(mainReference);
        ///results.sort((a, b) -> ((int) ((b.score - a.score) * 100)));

        System.out.println(results);
        System.out.println(best.score);

        if (best.score < mainReference.score) {
            System.out.println("Couldn't improve results");
            failedAttemptsToImprove++;

            if (failedAttemptsToImprove > maxFailedAttemptsToImprove) {
                System.out.println("Still using result as a reference");
                failedAttemptsToImprove = 0;
                mainReference = best;
            } else {
                lastResults.addAll(results);
            }

        } else {
            failedAttemptsToImprove = 0;
            mainReference = best;
        }

        NeuralNetwork.saveNetwork(best.network, "./", "backup");
        NeuralNetwork.saveNetwork(best.network, "./cache/" + name, iteration + "_" + ((int) best.score));

        var r = new Random();
        //settings.seed = r.nextInt();

        references = new ArrayList<>();

        var differenceMap = new NeuralNetworkDifference(new NeuralNetwork[]{
                results.get(0).network, results.get(1).network, results.get(2).network
        });

        if (singleSurvivor) {
            for (int i = 0; i < threads; i++) {
                var network = new NeuralNetwork(mainReference.network);
                // network.distort(r, distortProbability, distortValue);
                network.distort(r, distortProbability, distortValue);// 0.7 0.2
                //network.distort(r, differenceMap, 0.3, distortProbability, distortValue); // todo is more effective?

                references.add(network);
            }
        } else {
            for (int i = 0; i < threads; i++) {
                var network = new NeuralNetwork(results.get(i / copiesOfChildren).network);
                network.distort(r, distortProbability, distortValue); // 0.7 0.2
                references.add(network);
            }
        }

        var timeSpent = (System.currentTimeMillis() - startTime) / 1000f;
        System.out.println("Iteration took " + timeSpent + " seconds");

        iteration++;
    }

    synchronized boolean isFinished() {
        return references.size() == results.size();
    }

    void checkConsoleInput() {
        try {
            if (sc.ready()) {
                // System.out.println("Mutation - " + settings.mutationValue);
                // System.out.println("Duration - " + settings.duration);
                // System.out.println("Tests - " + settings.tests);
                // System.out.println("Seed - " + settings.seed);
                System.out.println("Program paused: type the command");
                System.out.println();

                sc.readLine();
                var line = sc.readLine();
                String[] data = line.split(" ");
                String command = data[0].toLowerCase(Locale.ROOT);

                switch (command) {
                    case "mut":
                        // settings.mutationValue = Double.parseDouble(data[1]);
                        // System.out.println("Mutation set - " + settings.mutationValue);
                        break;
                    //case "distort":
                    //    referenceNetwork.distort(new Random(), 1, Double.parseDouble(data[1]));
                    //    previousScore = 0;
                    //    changes = deepLearning.EvolutionTrainingThread.NeuralNetworkChanges.none;
                    //    break;
                    //case "forget":
                    //    referenceNetwork.forget(Double.parseDouble(data[1]));
                    //    previousScore = 0;
                    //    changes = deepLearning.EvolutionTrainingThread.NeuralNetworkChanges.none;
                    //    break;
                    //case "unbias":
                    //    referenceNetwork.unbias(Double.parseDouble(data[1]));
                    //    previousScore = 0;
                    //    changes = deepLearning.EvolutionTrainingThread.NeuralNetworkChanges.none;
                    //    break;
                    case "save":
                        NeuralNetwork.saveNetwork(mainReference.network, "saved", data[1]);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double[] countPerformance(String directory, NeuralNetworkTrainingData data) {
        class PerformanceRecord {
            final String filename;
            final double score;

            PerformanceRecord(String filename, double score) {
                this.filename = filename;
                this.score = score;
            }
        }

        try {

            File folder = new File(directory);

            // list all the files
            File[] files = folder.listFiles();
            List<PerformanceRecord> records = new ArrayList<>();
            for (File file : files) {
                if (file.isFile()) {
                    var ai = NeuralNetwork.loadNetwork(file.getAbsolutePath());
                    var cost = BackpropagationThread.countCost(ai, data);
                    records.add(new PerformanceRecord(file.getName(), ((-cost) * 1000)));
                    //System.out.println(file.getName() + "   performance -->  " + );
                }
            }

            records.sort((lhs, rhs) -> Double.compare(rhs.score, lhs.score));

            for (var record : records) {
                System.out.println(record.filename + "   performance -->  " + record.score);
            }

            double[] answer = new double[records.size()];
            for (int i = 0; i < records.size(); i++) {
                answer[i] = records.get(i).score;
            }
            return answer;
        } catch (Exception e) {
            e.getStackTrace();
        }

        return null;
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
}


