package deepLearning;

import java.io.Serializable;

public class NeuralNetworkData implements Serializable {
    int[] neuronsCount;
    double[][] biases;
    double[][][] weights;

    public NeuralNetworkData(int[] neuronsCount, double[][] biases, double[][][] weights) {
        this.neuronsCount = neuronsCount;
        this.biases = biases;
        this.weights = weights;
    }
}
