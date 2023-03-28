package experiments.ai.crumEater;

import engine.GameObject;
import graphics.Colors;
import graphics.Painter;
import math.Vector2d;

import static org.lwjgl.opengl.GL11.glColor4d;

public class Crumb extends GameObject {
    public final boolean poisonous;
    public final boolean food;

    public Crumb(Vector2d pos) {
        super(pos);
        this.poisonous = false;
        food = true;
    }

    public Crumb(Vector2d pos, boolean poisonous, boolean food) {
        super(pos);
        this.poisonous = poisonous;
        this.food = food;
    }

    @Override
    public void draw() {
        if (poisonous) {
            Colors.orange.glColor();
        } else if (food) {
            Colors.green.glColor();
        } else {
            glColor4d(0.5, 0.5, 0.5, 1);
        }
        Painter.filledCircle(pos, 0.004);
    }
}
