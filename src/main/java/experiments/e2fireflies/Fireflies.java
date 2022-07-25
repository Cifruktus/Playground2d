package experiments.e2fireflies;

import engine.Environment;
import graphics.window.MultisampledWindow;
import math.CustomMath;

import java.util.Random;

public class Fireflies {
    static final int firefliesCount = 300;
    static final double timePerStep = 0.05; // speed of the simulation

    public static void main(String[] args) {
        runSimulation();
    }

    public static void runSimulation() {
        var window = new MultisampledWindow();
        window.init();

        var env = new Environment();
        var r = new Random();

        for (int i = 0; i < firefliesCount; i++) {
            env.instantiate(new Firefly(
                    env.getRandomPlace(),
                    CustomMath.lerp(9, 10, r.nextDouble()),
                    r.nextDouble() * 10
            ));
        }

        while (!window.isClosed()) {
            window.updateScreen(() -> {
                env.update(timePerStep);
                env.draw();
            });
        }
    }
}
