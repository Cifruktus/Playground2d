package engine.collider;

import engine.GameObject;
import graphics.Painter;
import math.Vector2d;

public class CircleCollider extends Collider {
    public double radius;

    public CircleCollider(GameObject parent, double radius) {
        super(parent);
        this.radius = radius;
    }

    @Override
    public boolean isPointInGeometry(Vector2d point) {
        return parent.pos.sqrDistance(point) < radius * radius;
    }

    public void draw(){
        Painter.circle(parent.pos, radius);
    }
}
