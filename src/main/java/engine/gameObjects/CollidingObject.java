package engine.gameObjects;

import engine.Environment;
import engine.GameObject;
import engine.collider.CircleCollider;
import engine.collider.Collider;
import math.Vector2d;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public class CollidingObject extends GameObject {
    public CircleCollider collider;

    public CollidingObject(Vector2d pos, double radius) {
        super(pos);
        collider = new CircleCollider(this, radius);
    }

    @Override @MustBeInvokedByOverriders
    public void update(double dt, Environment environment) {

        for (var other : environment.getObjectOfType(pos, 0.1, CollidingObject.class)) {
            if (other == this) continue;

            var collision = Collider.calculateCollision(collider, other.collider);

            if (collision != null) {
                push(collision.vector, -collision.depth / 2);
                other.push(collision.vector, collision.depth / 2);
            }
        }

        super.update(dt, environment);
    }

    @Override @MustBeInvokedByOverriders
    public void draw() {
        //collider.draw();
    }

    public void push(Vector2d dir, double distance){
        pos = pos.add(dir.mul(distance));
    }
}
