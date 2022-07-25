package experiments.e3gravity;

import engine.Environment;
import graphics.window.MultisampledWindow;
import math.Vector2d;

import java.util.Random;

public class Gravity {

    static final int starsCount = 25;
    static final int subSteps = 100;
    static final double timePerStep = 0.05;

    public static void main(String[] args) {
        runSimulation();
    }

    public static void runSimulation() {
        var window = new MultisampledWindow();
        window.init();

        var env = new Environment();
        var r = new Random();

        for (int i = 0; i < starsCount; i++) {
            var star = new Star(
                    env.getRandomPlace(0.2),
                    new Vector2d(r.nextDouble() * 2 - 1, r.nextDouble() * 2 - 1).mul(0.01),
                    1 + r.nextDouble() * 10,
                    Star.colors[r.nextInt(Star.colors.length)]
            );

            star.velocity = star.velocity.add(Vector2d.fromDirection(star.pos.direction() + Math.PI / 2).mul(0.01));
            env.instantiate(star);
        }

        var centerOfMass = new CenterOfMass();
        env.instantiate(centerOfMass);
        env.cameraTarget = centerOfMass;

        while (!window.isClosed()) {
            window.updateScreen(() -> {
                for (int i = 0; i < subSteps; i++) {
                    env.update(timePerStep / subSteps);
                }

                env.draw();
            });
        }
    }
}
