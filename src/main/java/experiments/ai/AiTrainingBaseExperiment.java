package experiments.ai;

import deepLearning.NeuralNetwork;
import deepLearning.evolution.EvolutionTraining;
import deepLearning.evolution.EvolutionTrainingThread;
import engine.Environment;
import graphics.window.MultisampledWindow;

import java.util.Random;

public abstract class AiTrainingBaseExperiment {

    public NeuralNetwork ai;

    public abstract void init(Random r);
    public abstract void initForDemo();
    public abstract void update(double dt);
    public abstract void draw();
    public abstract double countResult();

    public void runDemo(NeuralNetwork ai, double simulationSpeed){
         var window = new MultisampledWindow();
         window.init();
         this.ai = ai;
         init(new Random());
         initForDemo();

         while (!window.isClosed()) {
             window.updateScreen(() -> {
                 update(simulationSpeed);
                 draw();
             });
         }

         window.close();
    }

    public double runTest(NeuralNetwork network, int testSeed, EvolutionTraining.TestSettings settings) {
        ai = network;
        init(new Random(testSeed));

        for (int i = 0; i < settings.duration; i++) {
            update(settings.timeframe);
        }

        return countResult();
    }
}
