package engine;

import engine.collider.Collider;
import graphics.Painter;
import math.Vector2d;
import org.jetbrains.annotations.Nullable;

public class GameObject {
    public GameObject(Vector2d pos) {
        this.pos = pos;
    }

    public Vector2d pos;
    public double angle;
    public boolean alive = true;
    @Nullable
    public Collider collider = null;

    public void preUpdate(double dt, Environment environment){}

    public void update(double dt, Environment environment){}

    public void draw() {
        Painter.square(pos.x, pos.y, 0.01);
    }
}
