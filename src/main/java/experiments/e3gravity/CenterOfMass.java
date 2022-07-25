package experiments.e3gravity;

import engine.Environment;
import engine.GameObject;
import math.Vector2d;

public class CenterOfMass extends GameObject {
    public CenterOfMass() {
        super(new Vector2d());
    }

    @Override
    public void update(double dt, Environment environment) {
        super.update(dt, environment);

        double mass = 0;
        Vector2d centerOfMass = new Vector2d();

        for (var star : environment.getObjectOfType(Star.class)) {
            centerOfMass = centerOfMass.add(star.pos.mul(star.mass));
            mass += star.mass;
        }

        pos = centerOfMass.div(mass);
    }

    @Override
    public void draw() {}
}
