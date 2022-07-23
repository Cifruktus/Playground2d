package engine;

import graphics.Color;
import graphics.Colors;
import math.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class Environment {
    List<GameObject> gameObjects;
    List<GameObject> addedObjects = new ArrayList<>();

    public Vector2d size = new Vector2d(1, 1);
    public Vector2d pos = new Vector2d(-0.5, -0.5);
    public double time = 0;

    public final Random r;
    public Color backgroundColor = Colors.background;

    public Environment() {
        gameObjects = new ArrayList<>();
        r = new Random();
    }

    public <T extends GameObject> List<T> getObjectOfType(Vector2d pos, double distance, Class<T> cl) {
        List<T> answer = new ArrayList<>();

        for (var obj : gameObjects) {
            if (cl.isInstance(obj)) {
                if (distance < 0 || pos.inSquareBounds(obj.pos, distance)) {
                    //noinspection unchecked
                    answer.add((T) obj);
                }
            }
        }

        return answer;
    }

    public List<GameObject> getObjectsFromDistance(Vector2d pos, double distance) {
        List<GameObject> answer = new ArrayList<>();

        for (var obj : gameObjects) {
            if (distance < 0 || pos.inSquareBounds(obj.pos, distance)) {
                answer.add(obj);
            }
        }

        return answer;
    }

    public <T extends GameObject> T getClosestObjectOfType(Vector2d pos, double maxDistance, Class<T> cl) {
        T closest = null;
        double minDist = maxDistance;

        var list = getObjectOfType(pos, maxDistance, cl);

        for (var obj : list) {
            var dist = pos.distance(obj.pos);

            if (dist < minDist) {
                minDist = dist;
                closest = obj;
            }
        }

        return closest;
    }

    public void instantiate(GameObject gameObject) {
        if (!inInBounds(gameObject.pos)) return;
        addedObjects.add(gameObject);
    }

    public void update(double dt) {
        for (var o : gameObjects) {
            o.preUpdate(dt, this);
        }

        for (var o : gameObjects) {
            o.update(dt, this);
        }

        for (var obj : gameObjects) {
            if (!Double.isFinite(obj.pos.x)) System.out.println(obj + " position has error" );
        }

        gameObjects.removeIf(object -> !object.alive);
        gameObjects.addAll(addedObjects);
        addedObjects = new ArrayList<>();

        time += dt;
    }

    public void draw() {
        backgroundColor.glClearColor();

        for (var o : gameObjects) {
            o.draw();
        }
    }

    boolean inInBounds(Vector2d obj) {
        if (pos.x > obj.x || pos.x + size.x < obj.x) return false;
        if (pos.y > obj.y || pos.y + size.y < obj.y) return false;
        return true;
    }

    // Random functions

    public boolean didHappen(double probability, double dt) {
        return r.nextDouble() < dt * probability;
    }

    public Vector2d getRandomPlace(){
        return pos.add(size.mul(
                new Vector2d(r.nextDouble(), r.nextDouble())
        ));
    }
}
