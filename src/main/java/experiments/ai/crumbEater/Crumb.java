package experiments.ai.crumbEater;

import engine.GameObject;
import graphics.Colors;
import graphics.Painter;
import math.Vector2d;

public class Crumb extends GameObject {
    public final boolean poisonous;

    public Crumb(Vector2d pos) {
        super(pos);
        this.poisonous = false;
    }

    public Crumb(Vector2d pos, boolean poisonous) {
        super(pos);
        this.poisonous = poisonous;
    }

    @Override
    public void draw() {
        if (poisonous) {
            Colors.orange.glColor();
        } else {
            Colors.green.glColor();
        }
        Painter.filledCircle(pos, 0.004);
    }
}
