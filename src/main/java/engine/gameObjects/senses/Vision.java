package engine.gameObjects.senses;

import engine.Environment;
import engine.GameObject;
import graphics.Painter;
import math.CustomMath;
import math.Vector2d;

import java.util.List;

import static java.lang.Math.*;

public class Vision {
    final static double minFovMod = 0.1;

    public final GameObject parent;
    public final double distance;
    public final double blurRadius = 0.2;
    public final double maxFov;
    public final int divisions;

    private double fov;

    public Vision(GameObject parent, double fov, double distance, int divisions) {
        this.parent = parent;
        this.maxFov = fov;
        this.fov = maxFov;
        this.divisions = divisions;
        this.distance = distance;
    }

    public void modifyFov(double mod){
        fov = maxFov * min(max(mod, minFovMod), 1);
    }

    public <K extends GameObject> double[] evaluate(Environment environment, Class<K> type) {
        var foodObjects = environment.getObjectOfType(parent.pos, distance, type);
        return evaluate(foodObjects, null);
    }

    public <K extends GameObject> double[] evaluate(Environment environment, Class<K> cl, VisionSelector<K> selector) {
        var foodObjects = environment.getObjectOfType(parent.pos, distance, cl);
        return evaluate(foodObjects, selector);
    }

    public <K extends GameObject> double[] evaluate(List<K> objects, VisionSelector<K> selector) {
        double[] distances = new double[divisions];

        for (var gameObject : objects) {
            var dist = parent.pos.distance(gameObject.pos);
            var influence = min(max((distance - dist) / distance, 0), 1);
            if (selector != null) influence *= selector.evaluate(gameObject);

            if (influence <= 0) continue;

            var objectAngle = gameObject.pos.sub(parent.pos).direction();

            int cell = sectorFromAngle(objectAngle);

            var angularDistance = CustomMath.wrapAngle(angleFromSector(cell, true) - objectAngle);
            var distanceFromCellCenter = angularDistance / (fov / divisions);
            var influenceMultiplier = 1 - min(1, max(0, (abs(distanceFromCellCenter) - 0.5)) / blurRadius);
            distances[cell] = max(distances[cell], influence * influenceMultiplier); // influence;

            if (cell > 0) {
                var nextCell = cell - 1;
                var distFromNextCell = distanceFromCellCenter - 1;
                influenceMultiplier = 1 - min(1, max(0, (abs(distFromNextCell) - 0.5)) / blurRadius);
                distances[nextCell] = max(distances[nextCell], influence * influenceMultiplier); // influence;
            }

            if (cell < divisions - 1) {
                var nextCell = cell + 1;
                var distFromNextCell = distanceFromCellCenter + 1;
                influenceMultiplier = 1 - min(1, max(0, (abs(distFromNextCell) - 0.5)) / blurRadius);
                distances[nextCell] = max(distances[nextCell], influence * influenceMultiplier); // influence;
            }
        }

        return distances;
    }

    double angleFromSector(int sector, boolean center) {
        return (((sector + (center ? 0.5 : 0.0)) / (double) divisions) * fov) + parent.angle - (fov / 2);
    }

    int sectorFromAngle(double objectAngle) {
        double direction = (objectAngle + 4 * PI - parent.angle + (fov / 2)) % (PI * 2);
        var cellVal = (direction / (fov)) * divisions;
        if (direction > PI + fov / 2) cellVal = 0;
        return max(min((int) Math.floor(cellVal), divisions - 1), 0);
    }

    public void draw() {
        for (int i = 0; i <= divisions; i++) {
            var direction = (((i + 0.0) / (double) divisions) * fov) + parent.angle - (fov / 2);
            Painter.line(parent.pos, parent.pos.add(Vector2d.fromDirection(direction).mul(0.1)));
        }

        for (int i = 0; i < divisions; i++) {
            var direction = (((i) / (double) divisions) * fov) + parent.angle - (fov / 2);
            var directionNext = (((i + 1) / (double) divisions) * fov) + parent.angle - (fov / 2);
            Painter.line(
                    parent.pos.add(Vector2d.fromDirection(directionNext).mul(distance)),
                    parent.pos.add(Vector2d.fromDirection(direction).mul(distance))
            );
        }
    }

    public void drawVisionData(double[] visionData) {
        if (visionData == null) return;
        for (int i = 0; i < divisions; i++) {
            var direction = (((i + 0.5) / (double) divisions) * fov) + parent.angle - (fov / 2);
            Painter.vector(parent.pos, Vector2d.fromDirection(direction).mul(0.1).mul(visionData[i]));
        }
    }
}
