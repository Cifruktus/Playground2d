package experiments.ai.crumEater;

import deepLearning.NeuralNetwork;
import deepLearning.evolution.EvolutionTraining;
import engine.Environment;
import engine.gameObjects.tools.ObjectPath;
import experiments.ai.AiTrainingBase;
import experiments.ai.AiTrainingBaseExperiment;

import java.io.FileNotFoundException;
import java.util.Random;

public class CrumbEaterExperiment extends AiTrainingBaseExperiment {

    static final int foodCount = 70;
    static final int minFoodCount = 20;

    int currentFood;
    Environment env;
    CrumbEater tested;

    public static void main(String[] args) throws FileNotFoundException {
        // To start from scratch
        new EvolutionTraining(new NeuralNetwork(new int[]{11,5,2}), new TrainingBase()).startFromScratch();

        // To continue training
        //new EvolutionTraining(NeuralNetwork.loadNetwork("cache/20230319_114205/1051_66884"), new TrainingBase()).continueFromSave();


         new TrainingBase().runDemo(NeuralNetwork.loadNetwork("dev/CrumEater1"), 0.05);
    }

    @Override
    public void init(Random r) {
        currentFood = r.nextInt(foodCount + 1 - minFoodCount) + minFoodCount;
        env = new Environment(r);

        for (int i = 0; i < currentFood; i++) {
            env.instantiate(new Crumb(env.getRandomPlace(0.1)));
        }

        tested = new CrumbEater(env.getRandomPlace(0.5), ai);

        env.instantiate(tested);
    }

    @Override
    public void initForDemo(){
        var trail = new ObjectPath(tested);
        env.instantiate(trail);
    }

    @Override
    public void update(double dt) {
        env.update(dt);

        if (env.getObjectOfType(null, -1, Crumb.class).size() < 5) {
            for (int g = 0; g < 5; g++) {
                env.instantiate(new Crumb(env.getRandomPlace(0.2)));
            }
        }
    }

    @Override
    public void draw() {
        env.draw();
    }

    @Override
    public double countResult() {
        return tested.crumsEaten;
    }

    private static class TrainingBase extends AiTrainingBase {
        @Override
        public AiTrainingBaseExperiment getExperiment() {
            return new CrumbEaterExperiment();
        }
    }
}
