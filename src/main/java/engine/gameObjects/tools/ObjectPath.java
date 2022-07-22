package engine.gameObjects.tools;

import engine.Environment;
import engine.GameObject;
import math.Vector2d;

import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.min;
import static org.lwjgl.opengl.GL11.*;

public class ObjectPath extends GameObject{
    int maxSize = 1000;
    final GameObject parent;
    final Queue<Vector2d> path = new LinkedList<>();

    public ObjectPath(GameObject parent) {
        super(parent.pos);
        this.parent = parent;
    }

    public void update(double dt, Environment e) {
        path.add(parent.pos);

        if (path.size() > maxSize) {
            path.poll();
            path.poll();
        }


    }

    public void draw(){
        glLineWidth(2);

        glBegin(GL_LINES);
        var pathArray = path.toArray(new Vector2d[]{});

        for (int i = 0; i < pathArray.length; i++) {
            var opacity = (i) / (double) min(maxSize, pathArray.length);

            opacity = min(1, opacity * 3);

            glColor4d(1,0,0, opacity);
            glVertex2d(pathArray[i].x, pathArray[i].y);
        }
        glEnd();
    }


}
