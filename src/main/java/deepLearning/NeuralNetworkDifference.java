package deepLearning;

import math.CustomMath;

import java.util.Arrays;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class NeuralNetworkDifference {
    int[] neuronsCount;

    double[][] avgBiases;
    double[][][] avgWeights;

    double[][] biasesDif;
    double[][][] weightsDif;

    public NeuralNetworkDifference(NeuralNetwork[] networks){
        neuronsCount = Arrays.copyOf(networks[0].neuronsCount, networks[0].neuronsCount.length);

        avgBiases = CustomMath.copy2dArray(networks[0].biases);
        avgWeights = CustomMath.copy3dArray(networks[0].weights);

        biasesDif = CustomMath.copy2dArray(networks[0].biases);
        weightsDif = CustomMath.copy3dArray(networks[0].weights);

        CustomMath.fill2dArray(avgBiases, 0);
        CustomMath.fill3dArray(avgWeights, 0);

        CustomMath.fill2dArray(biasesDif, 0);
        CustomMath.fill3dArray(weightsDif, 0);



        // filling with all weights / biases
        for (var network : networks) {
            for (int i = 0; i < network.weights.length; i++) {
                for (int j = 0; j < network.weights[i].length; j++) {
                    for (int k = 0; k < network.weights[i][j].length; k++) {
                        avgWeights[i][j][k] += network.weights[i][j][k];
                    }
                }
            }

            for (int i = 0; i < network.biases.length; i++) {
                for (int j = 0; j < network.biases[i].length; j++) {
                    avgBiases[i][j] += network.biases[i][j];
                }
            }
        }

        // dividing to networks count
        for (int i = 0; i < avgWeights.length; i++) {
            for (int j = 0; j < avgWeights[i].length; j++) {
                for (int k = 0; k < avgWeights[i][j].length; k++) {
                    avgWeights[i][j][k] /= networks.length;
                }
            }
        }

        for (int i = 0; i < avgBiases.length; i++) {
            for (int j = 0; j < avgBiases[i].length; j++) {
                avgBiases[i][j] /= networks.length;
            }
        }

        // filling with all differences
        for (var network : networks) {
            for (int i = 0; i < network.weights.length; i++) {
                for (int j = 0; j < network.weights[i].length; j++) {
                    for (int k = 0; k < network.weights[i][j].length; k++) {
                        weightsDif[i][j][k] += abs(avgWeights[i][j][k] - network.weights[i][j][k]);
                    }
                }
            }

            for (int i = 0; i < network.biases.length; i++) {
                for (int j = 0; j < network.biases[i].length; j++) {
                    biasesDif[i][j] += abs(avgBiases[i][j] - network.biases[i][j]);
                }
            }
        }

        // dividing to networks count
       /* for (int i = 0; i < avgWeights.length; i++) {
            for (int j = 0; j < avgWeights[i].length; j++) {
                for (int k = 0; k < avgWeights[i][j].length; k++) {
                    weightsDif[i][j][k] /= networks.length;
                }
            }
        }

        for (int i = 0; i < avgBiases.length; i++) {
            for (int j = 0; j < avgBiases[i].length; j++) {
                biasesDif[i][j] /= networks.length;
            }
        } */

        // finding max

        var weightsMaxDif = 0.0;
        for (int i = 0; i < avgWeights.length; i++) {
            for (int j = 0; j < avgWeights[i].length; j++) {
                for (int k = 0; k < avgWeights[i][j].length; k++) {
                    weightsMaxDif = max(weightsMaxDif, weightsDif[i][j][k]);
                }
            }
        }

        var biasesMaxDif = 0.0;
        for (int i = 0; i < avgBiases.length; i++) {
            for (int j = 0; j < avgBiases[i].length; j++) {
                biasesDif[i][j] /= networks.length;
                biasesMaxDif = max(biasesMaxDif, biasesDif[i][j]);
            }
        }

        // dividing to networks count
        for (int i = 0; i < avgWeights.length; i++) {
            for (int j = 0; j < avgWeights[i].length; j++) {
                for (int k = 0; k < avgWeights[i][j].length; k++) {
                    weightsDif[i][j][k] /= weightsMaxDif;
                }
            }
        }

        for (int i = 0; i < avgBiases.length; i++) {
            for (int j = 0; j < avgBiases[i].length; j++) {
                biasesDif[i][j] /= biasesMaxDif;
            }
        }

       /* System.out.println("**");
        for (var bias : biasesDif) {
            System.out.println(Arrays.toString(bias));
        }
        System.out.println("**");
        for (var a : weightsDif) {
            System.out.println("**");
            System.out.println("**");
            System.out.println("**");

            System.out.println("**");
            for (var v : a) {
                System.out.println(Arrays.toString(v));
            }

        } */
    }


}


