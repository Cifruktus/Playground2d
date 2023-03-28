package deepLearning.evolution;


import deepLearning.NeuralNetwork;
import deepLearning.NeuralNetworkModifier;

import java.util.Random;

public class EvolutionTrainingThread implements Runnable {

    static final int lastTestMultiplier = 50;

    public NeuralNetwork referenceNetwork;
    final EvolutionTraining lab;
    final EvolutionTraining.TestSettings settings;
    final int number;
    final LabTest test;

    public EvolutionTrainingThread(EvolutionTraining lab, LabTest test, NeuralNetwork referenceNetwork, EvolutionTraining.TestSettings settings, int number) {
        this.referenceNetwork = referenceNetwork;
        this.settings = settings;
        this.lab = lab;
        this.number = number;
        this.test = test;
    }


    double previousScore = 0;
    int failedAttempts = 0;
    int iteration = 0;


    NeuralNetworkModifier modifier;

    double increaseDifferenceModifier = 0.0;
    double defaultIncreaseDifference = 0.1;




    NeuralNetworkChanges changes = NeuralNetworkChanges.none;

    @Override
    public void run() {
        while (runIteration());

        var score = 0.0;

        for (int i = 0; i < settings.tests * lastTestMultiplier; i++) {
            score += test.runTest(referenceNetwork, 1000 + i, settings);
        }

        lab.reportProgress(number, 100.0);
        lab.taskFinished(referenceNetwork, score);
    }

    boolean runIteration() {
        // ********************
        // Modifying network

        lab.reportProgress(number, (iteration / (double) (
                settings.maxAttempts +
                        lastTestMultiplier * settings.tests)) * 100.0);

        NeuralNetwork network = null;

        switch (changes) {
            case none:
                network = new NeuralNetwork(referenceNetwork);
                break;
            case distort:
                network = new NeuralNetwork(referenceNetwork);

                var r = new Random();

                if (r.nextDouble() < network.weightsSum / ((double) network.weightsSum + network.biasesSum)) {
                    modifier = new NeuralNetworkModifier(r.nextInt(network.weightsSum),
                            true, (r.nextDouble() - 0.5) * settings.mutationValue);
                } else {
                    modifier = new NeuralNetworkModifier(r.nextInt(network.biasesSum),
                            false, (r.nextDouble() - 0.5) * settings.mutationValue);
                }

                network.modify(modifier);

                break;
            case increaseDifference:
                network = new NeuralNetwork(referenceNetwork);
                network.modify(modifier.multiply(increaseDifferenceModifier));
                break;
            case randomFill:
                network = new NeuralNetwork(referenceNetwork);
                network.randomFill(new Random());
                break;
        }

        // ********************
        // Testing

        var score = 0.0;

        for (int i = 0; i < settings.tests; i++) {
            score += test.runTest(network, settings.seed + i * 1123, settings);
        }

        // ********************
        // Evaluating


        if (score > previousScore) {
            previousScore = score;
            referenceNetwork = network;


            if (modifier != null) {
                increaseDifferenceModifier = defaultIncreaseDifference;
                changes = NeuralNetworkChanges.increaseDifference;
                //System.out.println("New score: " + ((int)score) + ", continuing");
            } else {
                changes = NeuralNetworkChanges.distort;
                //System.out.println("New score: " + ((int)score) + ", distorting");
            }


            failedAttempts = 0;
        } else {
            failedAttempts++;

            if (changes == NeuralNetworkChanges.increaseDifference) {
                if (Math.abs(increaseDifferenceModifier) > 0.05) {
                    increaseDifferenceModifier *= 0.6;
                    //System.out.println(score + " < " + previousScore + " - Returning back, reducing modifier");
                } else {
                    if (increaseDifferenceModifier > 0) {
                        increaseDifferenceModifier = -defaultIncreaseDifference * 0.5;
                        //System.out.println(score + " < " + previousScore + " - Returning back, reversing modifier");
                    } else {
                        modifier = null;
                        changes = NeuralNetworkChanges.distort;
                        //System.out.println(score + " < " + previousScore + " - Returning back");
                    }

                }
            } else {
                if (modifier != null && score != previousScore) {
                    increaseDifferenceModifier = -defaultIncreaseDifference * 0.5;
                    changes = NeuralNetworkChanges.increaseDifference;
                    //System.out.println(score + " < " + previousScore + " - Returning back, with negative modifier");
                } else {
                    changes = NeuralNetworkChanges.distort;
                    //System.out.println(score + " < " + previousScore + " - Returning back");
                }

            }

        }

        iteration++;

        if (failedAttempts > settings.failedAttemptsBeforeSeedReset || iteration > settings.maxAttempts) {
            //System.out.println("Task finished");
            //referenceNetwork = new NeuralNetwork(referenceNetwork);
            //referenceNetwork.unbias(settings.mutationValue * 0.1);
            //referenceNetwork.distort(new Random(), 1, settings.mutationValue * 0.15);

            changes = NeuralNetworkChanges.none;
            previousScore = 0;
            failedAttempts = 0;
            return false;
        }
        return true;
    }


    public interface LabTest{
        double runTest(NeuralNetwork network, int testSeed, EvolutionTraining.TestSettings settings);
    }

    enum NeuralNetworkChanges {
        none, randomFill, distort, increaseDifference
    }
}
