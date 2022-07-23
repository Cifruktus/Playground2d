package engine.gameObjects;

import engine.Environment;
import engine.GameObject;
import math.Vector2d;

public class KinematicModel extends GameObject {
    public double mass = 1;
    public double mInertia = 1;

    public Vector2d velocity = new Vector2d();
    public double angularVelocity = 0;

    public KinematicModel(Vector2d pos) {
        super(pos);
    }

    @Override
    public void update(double dt, Environment environment) {
        pos = pos.add(velocity.mul(dt));
        angle += angularVelocity * dt;

        super.update(dt, environment);
    }

    public void applyImpulse(Vector2d impulse){
        velocity = velocity.add(impulse.div(mass));
    }

    public void applyAngularImpulse(double impulse){
        angularVelocity += impulse / mInertia;
    }
}
