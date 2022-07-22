package engine.gameObjects.senses;

import engine.GameObject;
import math.Vector2d;

import static java.lang.Math.PI;
import static java.lang.Math.min;

public class ObjectSense {
    final GameObject parent;
    final double maxDistance;

    public ObjectSense(GameObject parent, double maxDistance) {
        this.parent = parent;
        this.maxDistance = maxDistance;
    }

    public double[] evaluate(GameObject object){
        var objectPos = object.pos.sub(parent.pos);
        var objectDir = objectPos.normalized();

        var distance = min(1, objectPos.distance() / maxDistance); // 0..1
        var parentDirections = calculateDirections(objectDir, parent.angle);
        var objectDirections = calculateDirections(objectDir.mul(-1), object.angle);

        return new double[]{
                distance,
                parentDirections[0],
                parentDirections[1],
                objectDirections[0],
                objectDirections[1],
        };
    }

    public double[] evaluatePos(Vector2d pos){
        var objectPos = pos.sub(parent.pos);
        var objectDir = objectPos.normalized();

        var distance = min(1, objectPos.distance() / maxDistance); // 0..1
        var parentDirections = calculateDirections(objectDir, parent.angle);

        return new double[]{
                distance,
                parentDirections[0],
                parentDirections[1],
        };
    }

    public double[] calculateDirections(Vector2d dir, double lookAngle){
        var lookDir = Vector2d.fromDirection(lookAngle);
        var rightDir = Vector2d.fromDirection(lookAngle + PI / 2);
        var forward = lookDir.dotProduct(dir); // -1..1
        var right = rightDir.dotProduct(dir); // -1..1

        return new double[]{forward / 2 + 0.5, right / 2 + 0.5};
    }
}
