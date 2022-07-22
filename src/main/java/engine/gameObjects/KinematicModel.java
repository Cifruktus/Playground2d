package engine.gameObjects;

import engine.Environment;
import engine.GameObject;
import math.Vector2d;

public class KinematicModel extends GameObject {
    double mass = 1;
    double mInertia = 1;

    Vector2d velocity = new Vector2d();
    double angularVelocity = 0;

    public KinematicModel(Vector2d pos) {
        super(pos);
    }

    @Override
    public void update(double dt, Environment environment) {
        pos = pos.add(velocity.mul(dt));

        super.update(dt, environment);
    }

    void applyImpulse(Vector2d impulse){
        velocity = velocity.add(impulse.div(mass));
    }

    void applyAngularImpulse(Double impulse){
        angularVelocity += impulse / mInertia;
    }
}
