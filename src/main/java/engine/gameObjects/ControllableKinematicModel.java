package engine.gameObjects;

import engine.Environment;
import engine.GameObject;
import graphics.Color;
import graphics.Painter;
import math.CustomMath;
import math.Vector2d;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.glColor3f;

public class ControllableKinematicModel extends GameObject {
    double maxAcceleration = 0.05;
    double maxAngularAcceleration = 1;

    double maxVelocity = 0.1;
    double maxAngularVelocity = 0.7;

    double velocity = 0;
    double angularVelocity = 0;

    double targetVelocity = 0;
    double targetAngularVelocity = 0;

    public ControllableKinematicModel(Vector2d pos) {
        super(pos);
    }

    public ControllableKinematicModel(Vector2d pos,
                                      double maxVelocity,
                                      double maxAngularVelocity,
                                      double maxAcceleration,
                                      double maxAngularAcceleration) {
        super(pos);
        this.maxAcceleration = maxAcceleration;
        this.maxAngularAcceleration = maxAngularAcceleration;
        this.maxVelocity = maxVelocity;
        this.maxAngularVelocity = maxAngularVelocity;
    }

    @Override
    public void update(double dt, Environment environment) {
        var realTargetVelocity = targetVelocity * maxVelocity;
        var realTargetAngularVelocity = targetAngularVelocity * maxAngularVelocity;

        if (abs(realTargetVelocity - velocity) < dt * maxAcceleration) {
            velocity = realTargetVelocity;
        } else {
            int accelerating = realTargetVelocity > velocity ? 1 : -1;
            velocity += accelerating * dt * maxAcceleration;
        }

        if (abs(realTargetAngularVelocity - angularVelocity) < dt * maxAcceleration) {
            angularVelocity = realTargetAngularVelocity;
        } else {
            int accelerating = realTargetAngularVelocity > angularVelocity ? 1 : -1;
            angularVelocity += accelerating * dt * maxAngularAcceleration;
        }

        angle += angularVelocity * dt;
        angle = CustomMath.wrapAngle(angle);

        //direction = Vector2d.fromCorrectDirection(angle);

        pos = pos.add(Vector2d.fromDirection(angle).mul(velocity * dt));


        super.update(dt, environment);
    }

    public double[] getNeuralControls() {
        return new double[]{targetVelocity, targetAngularVelocity / 2 + 0.5};
    }

    public void setControls(double vel, double angVel) {
        targetVelocity = min(1, max(0, vel));
        targetAngularVelocity = min(1, max(-1, angVel));
    }

    Color color = new Color(0xaaaaaa);

    public void draw() {
        glColor3f(0.9f, 0.2f, 0.2f);
        color.glColor();
        Painter.bird(pos.x, pos.y, 0.007, -angle + (PI / 2));
    }
}
