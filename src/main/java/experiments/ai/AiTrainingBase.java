package experiments.ai;

import deepLearning.NeuralNetwork;
import deepLearning.evolution.EvolutionTraining;
import deepLearning.evolution.EvolutionTrainingThread;

public abstract class AiTrainingBase implements EvolutionTrainingThread.LabTest {

    public void runDemo(NeuralNetwork network, double simulationSpeed){
        getExperiment().runDemo(network, simulationSpeed);
    }

    @Override
    public double runTest(NeuralNetwork network, int testSeed, EvolutionTraining.TestSettings settings) {
        return getExperiment().runTest(network, testSeed, settings);
    }

    public abstract AiTrainingBaseExperiment getExperiment();
}
