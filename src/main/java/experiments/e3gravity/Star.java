package experiments.e3gravity;

import engine.Environment;
import engine.gameObjects.KinematicModel;
import graphics.Color;

import graphics.Painter;
import math.Vector2d;

public class Star extends KinematicModel {
    static final double gravityCoefficient = 0.1e-5; // how much gravitation affects bodies
    static final double closestDistance = 0.005;
    static final Color[] colors = new Color[]{
            new Color(0xceedff),
            new Color(0xa1bfff),
            new Color(0xfcff8d),
            new Color(0xffcd49),
    };

    Color color;

    public Star(Vector2d pos, Vector2d velocity, double mass, Color color) {
        super(pos);
        this.velocity = velocity;
        this.mass = mass;
        this.color = color;
    }

    Vector2d appliedImpulse = new Vector2d();

    @Override
    public void preUpdate(double dt, Environment environment) {
        super.preUpdate(dt, environment);

        Vector2d force = new Vector2d();

        for (var star : environment.getObjectOfType(Star.class)) {
            if (star == this) continue;
            var direction = star.pos.sub(pos).normalized();
            var distance = star.pos.distance(this.pos);
            distance = Math.max(closestDistance, distance);
            force = force.add(direction.mul(gravityCoefficient * star.mass * this.mass / (Math.pow(distance, 2))));
        }

        appliedImpulse = force.mul(dt);
    }

    @Override
    public void update(double dt, Environment environment) {
        super.update(dt, environment);
        applyImpulse(appliedImpulse);
    }

    @Override
    public void draw() {
        var massSqrt = Math.sqrt(mass);

        color.glColor();
        Painter.filledCircle(pos, massSqrt * 0.003);
    }
}
