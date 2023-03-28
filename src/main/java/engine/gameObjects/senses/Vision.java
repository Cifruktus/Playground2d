package engine.gameObjects.senses;

import engine.Environment;
import engine.GameObject;
import experiments.ai.crumEater.Crumb;
import experiments.e1boids.Bird;
import graphics.Painter;
import math.CustomMath;
import math.Vector2d;

import java.util.List;

import static java.lang.Math.*;

public class Vision {
    public final GameObject parent;
    public final double distance;
    final double blurRadius = 0.2;
    final double fov;
    final int divisions;

    public Vision(GameObject parent, double fov, double distance, int divisions) {
        this.parent = parent;
        this.fov = fov;
        this.divisions = divisions;
        this.distance = distance;
    }

    public <K extends GameObject> double[] evaluate(Environment environment, Class<K> cl, VisionSelector<K> selector) {
        var foodObjects = environment.getObjectOfType(parent.pos, distance, cl);
        return evaluate(foodObjects, selector);
    }

    public <K extends GameObject> double focus_pos(double focus, List<K> objects) {
        double val = 0;

        var angularDist = 0.0;

        for (var food : objects) {
            var dist = parent.pos.distance(food.pos);

            var objectAngle = parent.pos.sub(food.pos).direction();

            var focusAngle = ((focus) * fov) + parent.angle - (fov / 2);


            // System.out.println(focus);
            double spreadAngle = (1.2 * fov / divisions) / 2;

            var angularDistance = CustomMath.wrapAngle(focusAngle - objectAngle);
            if (angularDistance > 1 || angularDistance < -1) continue;

            angularDist += angularDistance / (1.2 * fov / divisions);

        }

        return angularDist;
    }

    public <K extends GameObject> double focus(double focus, List<K> objects, VisionSelector<K> selector) {
        double val = 0;

        var focusAngle = getFocusDirection(focus);

        for (var food : objects) {
            var dist = parent.pos.distance(food.pos);
            var influence = min(max((distance - dist) / distance, 0), 1);
            influence *= selector.evaluate(food);

            if (influence <= 0) continue;

            var objectAngle = parent.pos.sub(food.pos).direction();




           // System.out.println(focus);

            var angularDistance = CustomMath.wrapAngle(focusAngle - objectAngle);
            var distanceFromCellCenter = angularDistance / (1.2 * fov / divisions);
            var influenceMultiplier = 1 - min(1, max(0, (abs(distanceFromCellCenter) - 0.5)) / blurRadius);
            val = max(val, influence * influenceMultiplier); // influence;
        }

        return val;
    }

    public double getFocusDirection(double focus){
        return ((focus) * fov) + parent.angle - (fov / 2);
    }

    public <K extends GameObject> double[] evaluate(List<K> objects, VisionSelector<K> selector) {

        double[] distances = new double[divisions];

        for (var food : objects) {
            var dist = parent.pos.distance(food.pos);
            var influence = min(max((distance - dist) / distance, 0), 1);
            influence *= selector.evaluate(food);

            if (influence <= 0) continue;

            var objectAngle = parent.pos.sub(food.pos).direction() + Math.PI;

            int cell = sectorFromAngle(objectAngle );

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

    public double[] evaluate(Environment environment) {
        var foodObjects = environment.getObjectOfType(parent.pos, distance, Crumb.class);

        double[] distances = new double[divisions];

        for (var food : foodObjects) {
            var dist = parent.pos.distance(food.pos);
            var influence = min(max((distance - dist) / distance, 0), 1);


            if (influence <= 0) continue;


            var objectAngle = food.pos.sub(parent.pos).direction();
            // Painter.line(parent.pos, parent.pos.add(Vec2.fromDirection(objectAngle).mul(0.1)));

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

    public double[] evaluate2(Environment environment) {
        var foodObjects = environment.getObjectOfType(parent.pos, distance, Bird.class);

        double[] distances = new double[divisions];

        for (var food : foodObjects) {
            var dist = parent.pos.distance(food.pos);
            var influence = min(max((distance - dist) / distance, 0), 1);


            if (influence <= 0) continue;


            var objectAngle = parent.pos.sub(food.pos).direction();
            // Painter.line(parent.pos, parent.pos.add(Vec2.fromDirection(objectAngle).mul(0.1)));

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

        // GL11.glColor3f(0,1,0);
        // for (int i = 0; i < divisions; i++) {
        //     var direction = (((i + 0.5) / (double) divisions) * fov) + parent.angle - (fov / 2);
        //     Painter.vector(parent.pos, Vec2.fromDirection(direction).mul(0.1).mul(distances[i]));
        // }

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
