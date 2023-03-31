package experiments.ai.crumbEater;

import deepLearning.NeuralNetwork;
import engine.Environment;
import engine.gameObjects.MovableModel;
import engine.gameObjects.senses.Vision;
import graphics.Colors;
import graphics.Painter;

import math.CustomMath;
import math.Vector2d;

import static java.lang.Math.*;
import static java.lang.Math.min;
import static org.lwjgl.opengl.GL11.*;

public class CrumbEater extends MovableModel {
    final NeuralNetwork ai; // neural networks that will determine actions of the CrumbEater
    final Vision vision = new Vision(this, PI, 0.1, 8); // Vision, what CrumbEater can see
    final double maxDistanceFromCenter = 0.7;

    // stats
    int crumsEaten = 0;
    public double distancePassed = 0;

    // updated every [update]
    double[] visionData;

    public CrumbEater(Vector2d pos, NeuralNetwork ai) {
        super(pos, 0.03 * 3, Math.PI * 1);
        this.ai = ai;
    }

    @Override
    public void update(double dt, Environment environment) {
        double angleToCenter = CustomMath.wrapAngle(pos.sub(environment.getCenter()).direction() - angle);
        double distanceFromCenter = pos.sub(environment.getCenter()).distance();
        double distanceFromCenter4Angle = min(1, pos.sub(environment.getCenter()).distance() / maxDistanceFromCenter);

        visionData = vision.evaluate(environment, Crumb.class);

        // Creating input and running neural network
        double[] answer = ai.run(new double[]{
                // Vision part of the input, information about where crumbs are located
                visionData[4],
                visionData[5],
                visionData[6],
                visionData[7],
                visionData[0],
                visionData[1],
                visionData[2],
                visionData[3],
                // Location, to not lose the center of the play field
                CustomMath.sigmoid(abs(distanceFromCenter4Angle * angleToCenter / PI)),
                CustomMath.sigmoid(distanceFromCenter - 0.5),
                // Time, to not get stuck if other inputs are the same
                CustomMath.sigmoid(sin(environment.time / 2))
        });

        var rotation = (answer[0] - 0.5) * 2; // output 0 of neural network, where it wants to turn
        var speed = answer[1]; // output 1 of neural network, at which speed it wants to move

        moveTo(Vector2d.fromDirection(angle + (maxAngularSpeed * rotation * dt)), speed);

        // If we close enough to the crumb, delete it from play field, add eaten to the stats
        var taken = environment.getClosestObjectOfType(pos, 0.013, Crumb.class);

        if (taken != null) {
            taken.alive = false;
            crumsEaten ++;
        }
        distancePassed = desiredSpeed * maxSpeed * dt;

        super.update(dt, environment);
    }

    public void draw() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Colors.yellow.withAlpha(0.2).glColor();
        vision.draw();
        Colors.orange.withAlpha(0.2).glColor();;
        vision.drawVisionData(visionData);

        glDisable(GL_BLEND);

        Colors.red.glColor();
        Painter.bird(pos.x, pos.y, 0.007, angle);
    }
}