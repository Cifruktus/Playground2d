package engine.gameObjects.tools;

import engine.Environment;
import engine.GameObject;
import graphics.Colors;
import math.Vector2d;

import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.min;
import static org.lwjgl.opengl.GL11.*;

public class ObjectPath extends GameObject {
    static final int skipEveryNUpdate = 3;
    int maxSize = 1000;
    final GameObject parent;
    final Queue<Vector2d> path = new LinkedList<>();

    public ObjectPath(GameObject parent) {
        super(parent.pos);
        this.parent = parent;
    }

    int updates = 0;

    public void update(double dt, Environment e) {
        updates++;
        if (updates % skipEveryNUpdate != 0) return;

        path.add(parent.pos);

        if (path.size() > maxSize) {
            path.poll();
            path.poll();
        }


    }

    public void draw(){
        glLineWidth(2);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //glBlendFunc(GL_ONE, GL_ONE);


        glBegin(GL_LINES);
        var pathArray = path.toArray(new Vector2d[]{});

        for (int i = 0; i < pathArray.length; i++) {
            var opacity = (i) / (double) min(maxSize, pathArray.length);

            opacity = min(1, opacity * 3);

            Colors.red.glColor(opacity);

            glVertex2d(pathArray[i].x, pathArray[i].y);
        }
        glEnd();

        glDisable(GL_BLEND);
    }


}
