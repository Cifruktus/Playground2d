package experiments.e2fireflies;

import engine.Environment;
import engine.GameObject;
import graphics.Colors;
import graphics.Painter;
import math.Vector2d;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class Firefly extends GameObject {

    static final double seeingDist = 0.3;

    double period;
    double time;

    public Firefly(Vector2d pos, double period, double time) {
        super(pos);
        this.period = period;
        this.time = time;
    }

    @Override
    public void update(double dt, Environment environment) {
        super.update(dt, environment);

        var fireflies = environment.getObjectOfType(pos, seeingDist, Firefly.class);

        var influenceSum = 0.0;
        var otherstime = 0.0; // average time until blink for surrounding fireflies

        for (var firefly : fireflies) {
            var dist = pos.distance(firefly.pos);

            var influence = max((seeingDist - dist) / seeingDist, 0);

            otherstime += influence * firefly.time;
            influenceSum += influence;
        }

        otherstime /= influenceSum;

        if (otherstime > time) { // if we are behind of others, speed up our blink timer
            time += period * 0.002;
        } else { // if we are ahead of others, slow down our blink timer
            time -= period * 0.002;
        }
        time = (time + dt) % period;
    }

    @Override
    public void draw() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        double val = time / period;

        var invertedVal = val;
        val = 1 - val;
        val *= val * val;
        invertedVal = Math.pow(invertedVal, 15);

        val = max(val, invertedVal);

        Colors.yellow.withAlpha(max(val, 0.25)).glColor();
        Painter.filledCircle(pos, max(val, 0.25) * 0.015);
    }
}
