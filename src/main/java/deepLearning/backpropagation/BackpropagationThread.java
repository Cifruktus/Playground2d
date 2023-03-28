package deepLearning.backpropagation;


import deepLearning.NeuralNetwork;
import deepLearning.NeuralNetworkTrainingData;
import math.CustomMath;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.abs;

public class BackpropagationThread implements Runnable {
    static double trainingStep = 0.4;

    public NeuralNetwork referenceNetwork;
    final BackpropagationTraining lab;

   final NeuralNetworkTrainingData data;
   final NeuralNetworkTrainingData fullData;

   final int iterations;
   int number;

    public BackpropagationThread(BackpropagationTraining lab, NeuralNetworkTrainingData fullData, NeuralNetworkTrainingData data, NeuralNetwork referenceNetwork, double trainingStep, int iterations, int number) {
        this.referenceNetwork = referenceNetwork;
        this.fullData = fullData;
        this.data = data;
      //  this.trainingStep = trainingStep;
        this.iterations = iterations;
        this.number = number;

        this.lab = lab;
    }

    @Override
    public void run() {
        double partialDataTrainings = 0.8;

        for (int i = 0; i < iterations * 0.8; i++) {
             referenceNetwork = train(referenceNetwork, data);

             if (i % 100 == 0) {
                 lab.reportProgress(number, (80.0 * i) / iterations);
             }
        }

        for (int i = 0; i < iterations * 0.2; i++) {
            referenceNetwork = train(referenceNetwork, fullData);

            if (i % 100 == 0) {
                lab.reportProgress(number, 80 + (20.0 * i) / iterations);
            }
        }

        var cost = countCost(referenceNetwork, fullData);

        lab.reportProgress(number, 100.0);
        lab.taskFinished(referenceNetwork, -cost * 1000);
    }

    public static double countCost(NeuralNetwork n, NeuralNetworkTrainingData data){
        return countCost(n, data, null);
    }

    public static double countCost(NeuralNetwork n, NeuralNetworkTrainingData data, double[] modifier){
        var cost = 0.0;

        for (int test = 0; test < data.examples.size(); test++) {
            var inputData = data.examples.get(test).input;
            var outputData = data.examples.get(test).output;

            if (modifier != null) inputData = CustomMath.sum(inputData, modifier);

            n.run2(inputData);

            for (int i = 0; i < n.activations[n.activations.length - 1].length; i++) {
                var difference = (n.activations[n.activations.length - 1][i] - outputData[i]);
                cost += difference * difference;
            }
        }

        return cost;
    }

    public static NeuralNetwork train(NeuralNetwork n, NeuralNetworkTrainingData data) {
        var out = new NeuralNetwork(n);

        for (int test = 0; test < data.examples.size(); test++) {
            var inputData = data.examples.get(test).input;
            var outputData = data.examples.get(test).output;

            var biasesCd = CustomMath.copy2dArray(n.biases);
            var activationDe = CustomMath.copy2dArray(n.activations);
            CustomMath.fill2dArray(activationDe, 0);

            var cost = 0.0;

            var weightsCd = CustomMath.copy3dArray(n.weights);
            CustomMath.fill3dArray(weightsCd, 0);

            var activationsBeforeMod = n.run2(inputData);

            for (int i = 0; i < activationDe[activationDe.length - 1].length; i++) {
                var difference = (n.activations[activationDe.length - 1][i] - outputData[i]);
                activationDe[activationDe.length - 1][i] = 2 * difference;
                cost += difference * difference;
            }

            //System.out.println(cost);

            for (int l = n.neuronsCount.length - 1; l > 0; l--) {
                for (int output = 0; output < n.activations[l].length; output++) {
                    double derOfMod = CustomMath.sigmoidDerivative(activationsBeforeMod[l][output]);
                    biasesCd[l][output] = derOfMod * activationDe[l][output];

                    for (int input = 0; input < n.activations[l - 1].length; input++) {
                        var weight = n.weights[l - 1][input][output];
                        weightsCd[l - 1][input][output] = n.activations[l - 1][input] * derOfMod * activationDe[l][output];
                        activationDe[l - 1][input] += weight * derOfMod * activationDe[l][output];
                    }
                }
            }

            // System.out.println(Arrays.deepToString(activationDe));

            out.add(weightsCd, biasesCd, -trainingStep / data.examples.size() );
        }


        return out;
    }

    public static double[] getInputImportance(NeuralNetwork n, NeuralNetworkTrainingData data, int iterations) {
        double[] inputAvg = new double[data.examples.get(0).input.length];

        for (var example : data.examples) {
            inputAvg = CustomMath.sum(inputAvg, example.input);
        }
        CustomMath.divideArray(inputAvg, data.examples.size());

        double[]  deviation = new double[data.examples.get(0).input.length];

        for (var example : data.examples) {
            var currentDeviation = new double[deviation.length];
            for (int i = 0; i < currentDeviation.length; i++) {
                currentDeviation[i] = abs(example.input[i] - inputAvg[i]);
            }
            deviation = CustomMath.sum(deviation, currentDeviation);
        }
        CustomMath.divideArray(deviation, data.examples.size());

        System.out.println(Arrays.toString(deviation));

        Random r = new Random();

        var referenceCost = countCost(n, data);

        var costPerInput = new double[deviation.length];

        for (int iteration = 0; iteration < iterations; iteration++) {
            System.out.println("Iteration " + iteration);
            for (int i = 0; i < deviation.length; i++) {
                var modifier = r.nextDouble() + 0.05;
                modifier *= r.nextBoolean() ? 1 : -1;

                var modifierArray = new double[deviation.length];
                modifierArray[i] = modifier * deviation[i];

                var cost = countCost(n, data, modifierArray);

                costPerInput[i] += abs((cost - referenceCost) / modifier); ////
            }
        }

        CustomMath.divideArray(costPerInput, iterations);

        return costPerInput;
    }
}
