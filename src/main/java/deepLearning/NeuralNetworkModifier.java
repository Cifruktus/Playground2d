package deepLearning;

public class NeuralNetworkModifier {
    final int id;
    final boolean weightModify;
    final double value;

    public NeuralNetworkModifier(int id, boolean weightModify, double value) {
        this.id = id;
        this.weightModify = weightModify;
        this.value = value;
    }

    public NeuralNetworkModifier multiply(double value){
        return new NeuralNetworkModifier(id, weightModify, value * this.value);
    }
}
