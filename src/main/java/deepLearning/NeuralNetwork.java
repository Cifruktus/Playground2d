package deepLearning;

import math.CustomMath;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NeuralNetwork implements Serializable {
    public int[] neuronsCount;
    public int biasesSum;
    public int weightsSum;

    public double[][] biases;
    public double[][] activations;
    public double[][][] weights;

    public static NeuralNetwork fromData(NeuralNetworkData data) {
        var network = new NeuralNetwork(data.neuronsCount);
        network.biases = data.biases;
        network.weights = data.weights;
        return network;
    }

    public NeuralNetwork(NeuralNetwork original) {
        neuronsCount = Arrays.copyOf(original.neuronsCount, original.neuronsCount.length);
        biases = CustomMath.copy2dArray(original.biases);
        activations = CustomMath.copy2dArray(original.activations);
        weights = CustomMath.copy3dArray(original.weights);
        biasesSum = original.biasesSum;
        weightsSum = original.weightsSum;
    }

    public NeuralNetwork(int[] neuronsCount) {
        assert (neuronsCount.length > 1);
        this.neuronsCount = neuronsCount;

        biases = new double[neuronsCount.length][];

        for (int i = 0; i < neuronsCount.length; i++) {
            biases[i] = new double[neuronsCount[i]];
        }

        activations = new double[neuronsCount.length][];

        for (int i = 0; i < neuronsCount.length; i++) {
            activations[i] = new double[neuronsCount[i]];
        }



        weights = new double[neuronsCount.length - 1][][];

        for (int i = 0; i < weights.length; i++) {
            weights[i] = new double[neuronsCount[i]][neuronsCount[i + 1]];
        }

        biasesSum = 0;
        for (int j : neuronsCount) {
            biasesSum += j;
        }

        weightsSum = 0;
        for (int i = 1; i < neuronsCount.length; i++) {
            weightsSum += neuronsCount[i] * neuronsCount[i - 1];
        }
    }

    public void project(NeuralNetwork network) {
        for (int i = 0; i < min(weights.length, network.weights.length); i++) {
            for (int j = 0; j < min(weights[i].length, network.weights[i].length); j++) {
                for (int k = 0; k < min(weights[i][j].length, network.weights[i][j].length); k++) {
                    weights[i][j][k] = network.weights[i][j][k];
                }
            }
        }

        for (int i = 0; i < min(biases.length, network.biases.length); i++) {
            for (int j = 0; j < min(biases[i].length, network.biases[i].length); j++) {
                biases[i][j] = network.biases[i][j];
            }
        }
    }

    public void add(double[][][] weightsMod, double[][] biasesMod, double multiplier) {
        for (int i = 0; i < weightsMod.length; i++) {
            for (int j = 0; j < weightsMod[i].length; j++) {
                for (int k = 0; k < weightsMod[i][j].length; k++) {
                    weights[i][j][k] += weightsMod[i][j][k] * multiplier;
                }
            }
        }

        for (int i = 0; i < biasesMod.length; i++) {
            for (int j = 0; j < biasesMod[i].length; j++) {
                biases[i][j] += biasesMod[i][j] * multiplier;
            }
        }
    }

    public void distort(Random r, NeuralNetworkDifference multiplier, double minDistort, double probability, double amount) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    if (r.nextDouble() < probability) {
                        weights[i][j][k] += (max(multiplier.weightsDif[i][j][k], minDistort) * amount * (r.nextDouble() - 0.5));
                    }

                }
            }
        }

        for (int i = 0; i < biases.length; i++) {
            for (int j = 0; j < biases[i].length; j++) {
                if (r.nextDouble() < probability) {
                    biases[i][j] += (multiplier.biasesDif[i][j] * amount * (r.nextDouble() - 0.5));
                }
            }
        }
    }

    public void distort(Random r, double probability, double amount) {


        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    if (r.nextDouble() < probability) {
                        weights[i][j][k] += (amount * (r.nextDouble() - 0.5));
                    }

                }
            }
        }

        for (int i = 0; i < biases.length; i++) {
            for (int j = 0; j < biases[i].length; j++) {
                if (r.nextDouble() < probability) {
                    biases[i][j] += (amount * (r.nextDouble() - 0.5));
                }
            }
        }
    }

    public void modifyWeight(int index, double modifier) {
        if (index >= weightsSum) throw new ArrayIndexOutOfBoundsException();

        int layer = 0;

        while (index >= neuronsCount[layer] * neuronsCount[layer + 1]) {
            index -= neuronsCount[layer] * neuronsCount[layer + 1];
            layer++;
        }


        int inIndex = index % weights[layer].length;
        int outIndex = index / weights[layer].length;

        var prevWeight = weights[layer][inIndex][outIndex];
        weights[layer][inIndex][outIndex] = weights[layer][inIndex][outIndex] + modifier;
        var currentWeight = weights[layer][inIndex][outIndex];

        biases[layer + 1][outIndex] += (0.5 * currentWeight) - (0.5 * prevWeight);
    }

    public void modifyBias(int index, double modifier) {
        if (index >= biasesSum) throw new ArrayIndexOutOfBoundsException();

        int layer = 0;

        while (index >= neuronsCount[layer]) {
            index -= neuronsCount[layer];
            layer++;
        }

        biases[layer][index] = biases[layer][index] * (1 + modifier) + modifier;
    }

    public void modify(NeuralNetworkModifier modifier) {
        if (modifier.weightModify) {
            modifyWeight(modifier.id, modifier.value);
        } else {
            modifyBias(modifier.id, modifier.value);
        }
    }

    public void randomFill(Random r) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] = r.nextDouble() * 2 - 1;
                }
            }
        }

        for (int layer = 1; layer < biases.length; layer++) {
            for (int output = 0; output < biases[layer].length; output++) {
                double initialSum = 0;

                for (int input = 0; input < biases[layer - 1].length; input++) {
                    initialSum += weights[layer - 1][input][output] * 0.5;
                }

                biases[layer][output] = -initialSum;
            }
        }

        //for (int i = 0; i < biases.length; i++) {
        //    for (int j = 0; j < biases[i].length; j++) {
        //        biases[i][j] = r.nextDouble() * 2 - 1; // todo dont use random range here
        //    }
        //}
    }

    public NeuralNetworkData toData() {
        return new NeuralNetworkData(neuronsCount, biases, weights);
    }

    public double[][] run2(double[] inputData) {
        assert (activations[0].length == inputData.length);
        var activationsBeforeMod = CustomMath.copy2dArray(activations);

        activations[0] = inputData;


        for (int layer = 1; layer < activations.length; layer++) {
            //  var weights = this.weights[layer - 1];

            for (int output = 0; output < activations[layer].length; output++) {
                double value = biases[layer][output];

                for (int input = 0; input < activations[layer - 1].length; input++) {
                    value += weights[layer - 1][input][output] * activations[layer - 1][input];
                }

                activationsBeforeMod[layer][output] = value;
                activations[layer][output] = CustomMath.sigmoid(value);
            }
        }

        return activationsBeforeMod;
    }

    public double[] run(double[] inputData) {
        assert (activations[0].length == inputData.length);
        activations[0] = inputData;

        for (int layer = 1; layer < activations.length; layer++) {
            //  var weights = this.weights[layer - 1];

            for (int output = 0; output < activations[layer].length; output++) {
                double value = biases[layer][output];

                for (int input = 0; input < activations[layer - 1].length; input++) {
                    value += weights[layer - 1][input][output] * activations[layer - 1][input];
                }

                activations[layer][output] = CustomMath.sigmoid(value);
            }
        }

        return activations[activations.length - 1];
    }

    public void unbias(double value) {
        for (int layer = 1; layer < biases.length; layer++) {
            for (int output = 0; output < biases[layer].length; output++) {
                double initialSum = 0;

                for (int input = 0; input < biases[layer - 1].length; input++) {
                    initialSum += weights[layer - 1][input][output] * 0.5;
                }

                biases[layer][output] = CustomMath.lerp(biases[layer][output], -initialSum, value);
            }
        }
    }

    public void forget(double value) {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {
                    weights[i][j][k] *= 1 - value;
                }
            }
        }

        for (int i = 0; i < biases.length; i++) {
            for (int j = 0; j < biases[i].length; j++) {
                biases[i][j] *= 1 - value;

            }
        }
    }

    public static void saveNetwork(NeuralNetwork network, String path, String file) {
        try {
            Files.createDirectories(Paths.get(path));
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path + "/" +file));
            out.writeObject(network.toData());
            out.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static NeuralNetwork loadNetwork(String file) {
        NeuralNetwork network = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            network = NeuralNetwork.fromData((NeuralNetworkData) in.readObject());
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return network;
    }

    @Override
    public String toString() {
        return "NeuralNetwork {" +
                "\nneuronsCount=" + Arrays.toString(neuronsCount) +
                "\nbiasesSum=" + biasesSum +
                "\nweightsSum=" + weightsSum +
                "\nbiases=" + Arrays.deepToString(biases) +
                "\nactivations=" + Arrays.deepToString(activations) +
                "\nweights=" + Arrays.deepToString(weights) +
                "\n}";
    }
}
