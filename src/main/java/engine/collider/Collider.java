package engine.collider;

import engine.GameObject;
import math.Vector2d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.sqrt;

public abstract class Collider {
    public final GameObject parent;

    public Collider(@NotNull GameObject parent) {
        this.parent = parent;
    }

    public abstract boolean isPointInGeometry(Vector2d point);

    @Nullable
    public static Collision calculateCollision(CircleCollider c1, CircleCollider c2) {
        double maxDist = c1.radius + c2.radius;
        var sqrDist = c1.parent.pos.sqrDistance(c2.parent.pos);
        if (sqrDist < maxDist * maxDist) {
            var dist = sqrt(sqrDist);
            double depth = maxDist - dist;

            var dir = c2.parent.pos.sub(c1.parent.pos).div(dist);
            var center = c1.parent.pos.add(dir.mul(c1.radius - depth / 2));

            return new Collision(center, dir, depth);
        }
        return null;
    }

    public static class Collision {
        public final Vector2d center;
        public final Vector2d vector;
        public final double depth;

        public Collision(Vector2d center, Vector2d vector, double depth) {
            this.center = center;
            this.vector = vector;
            this.depth = depth;
        }
    }
}
