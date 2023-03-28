package experiments.ai.crumEater;

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
    NeuralNetwork ai;

    Vision vision = new Vision(this, PI , 0.1, 8);;

    double maxDistanceFromCenter = 0.7;

    int crumsEaten = 0;

    public CrumbEater(Vector2d pos, NeuralNetwork ai) {
        super(pos, 0.03 * 3, Math.PI * 1);
        this.ai = ai;
    }

    public double distancePassed = 0;


    double[] visionData;

    @Override
    public void update(double dt, Environment environment) {
        double angleToCenter = CustomMath.wrapAngle(pos.sub(environment.getCenter()).direction() - angle);
        double distanceFromCenter = pos.sub(environment.getCenter()).distance();
        double distanceFromCenter4Angle = min(1, pos.sub(environment.getCenter()).distance() / maxDistanceFromCenter);

        visionData = vision.evaluate(environment);

        double[] answer = ai.run(new double[]{
                visionData[4],
                visionData[5],
                visionData[6],
                visionData[7],
                visionData[0],
                visionData[1],
                visionData[2],
                visionData[3],
                CustomMath.sigmoid(abs(distanceFromCenter4Angle * angleToCenter / PI)),
                CustomMath.sigmoid(distanceFromCenter - 0.5),
                CustomMath.sigmoid(sin(environment.time / 2))
        });


        var rotation = (answer[0] - 0.5) * 2;
        var speed = answer[1];

        moveTo(Vector2d.fromDirection(angle + (maxAngularSpeed * rotation * dt)), speed);

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