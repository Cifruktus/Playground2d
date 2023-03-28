package deepLearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NeuralNetworkTrainingData {
    public List<TrainingEntry> examples = new ArrayList<>();

    public NeuralNetworkTrainingData() {}
    public NeuralNetworkTrainingData(List<TrainingEntry> examples) {
        this.examples = examples;
    }

    public void addNewRecord(double[] inputRec, double[] outputRec){
       examples.add(new TrainingEntry(inputRec, outputRec));
    }

    public NeuralNetworkTrainingData getFraction(double fraction){
        Collections.shuffle(examples);
        var sublist = examples.subList(0, (int) (examples.size() * fraction));
        return new NeuralNetworkTrainingData(new ArrayList<>(sublist));
    }

    public static class TrainingEntry {
        public final double[] input;
        public final double[] output;

        TrainingEntry(double[] input, double[] output) {
            this.input = input;
            this.output = output;
        }
    }
}
