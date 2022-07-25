package experiments.e1boids;

import engine.Environment;
import graphics.window.MultisampledWindow;

public class Boids {
    static final int birdsCount = 120;
    static final double timePerStep = 0.1; // speed of the simulation
    public static void main(String[] args) {
        runSimulation();
    }

    public static void runSimulation() {
        var window = new MultisampledWindow();
        window.init();

        var env = new Environment();

        for (int i = 0; i < birdsCount; i++) {
            env.instantiate(new Bird(env.getRandomPlace()));
        }

        while (!window.isClosed()) {
            window.updateScreen(() -> {
                env.update(timePerStep);
                env.draw();
            });
        }
    }
}
