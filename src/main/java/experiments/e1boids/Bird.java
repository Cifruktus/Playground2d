package experiments.e1boids;

import engine.GameObject;
import engine.Environment;
import graphics.Color;
import graphics.Colors;
import graphics.Painter;
import math.Vector2d;

import java.util.Random;

public class Bird extends GameObject {
    static final double visionRadius = 0.05;
    static final double birdSpeed = 0.03;

    Random r;

    protected Vector2d direction; // moving direction
    double speed;

    Color color;

    public Bird(Vector2d pos) {
        super(pos);
        r = new Random();
        direction = new Vector2d(r.nextDouble() - 0.5, r.nextDouble() - 0.5).normalized();
        speed = birdSpeed;

        color = Colors.cyan.lerp(Colors.blue, r.nextDouble());
    }

    @Override
    public void update(double dt, Environment environment) {
        // neighbours that this particular bird can see
        var birds = environment.getObjectOfType(pos, visionRadius, Bird.class);

        int birdsSeen = 0;

        var birdsMoveDir = new Vector2d(); // average moving direction of birds around

        var closestBirdDist = visionRadius;
        var closestBirdPos = new Vector2d();

        for (var bird : birds) {
            if (bird == this) continue;
            var dist = pos.distance(bird.pos);
            if (dist > visionRadius) continue;

            birdsMoveDir = birdsMoveDir.add(bird.direction);

            if (closestBirdDist > dist) {
                closestBirdDist = dist;
                closestBirdPos = bird.pos.sub(pos);
            }

            birdsSeen++;
        }

        direction = direction.add(new Vector2d(r.nextDouble() - 0.5, r.nextDouble() - 0.5).mul(0.1)).normalized();

        if (birdsSeen > 0) {
            birdsMoveDir = birdsMoveDir.mul(1.0 / birdsSeen);
            direction = direction.add(birdsMoveDir.mul(0.1)).normalized();

            if (closestBirdDist < 0.02) { // If we're too close, steer away
                direction = direction.add(closestBirdPos.normalized().mul(-0.06)).normalized();
            } else if (closestBirdDist > 0.02) { // If we're too far, steer closer
                direction = direction.add(closestBirdPos.normalized().mul(0.03)).normalized();
            }
        }

        // avoiding screen borders
        if (environment.pos.x + 0.2 > pos.x) {
            direction = direction.add(new Vector2d(1, 0).mul(0.05)).normalized();
        }

        if (environment.pos.x + environment.size.x - 0.2 < pos.x) {
            direction = direction.add(new Vector2d(-1, 0).mul(0.05)).normalized();
        }

        if (environment.pos.y + 0.2 > pos.y) {
            direction = direction.add(new Vector2d(0, 1).mul(0.05)).normalized();
        }

        if (environment.pos.y + environment.size.y - 0.2 < pos.y) {
            direction = direction.add(new Vector2d(0, -1).mul(0.05)).normalized();
        }

        pos = pos.add(direction.mul(speed * dt));

        super.update(dt, environment);
    }

    @Override
    public void draw() {
        color.glColor();
        Painter.bird(pos.x, pos.y, 0.005, direction.direction());
    }
}
