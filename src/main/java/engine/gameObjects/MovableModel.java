package engine.gameObjects;

import engine.Environment;
import engine.GameObject;
import graphics.Painter;
import math.CustomMath;
import math.Vector2d;

import static org.lwjgl.opengl.GL11.glColor4d;

public class MovableModel extends GameObject {
    final double maxSpeed;
    final double maxAngularSpeed;

    public double angle;

    double desiredSpeed = 1;
    Vector2d desiredDir = new Vector2d(0,0);

    public MovableModel(Vector2d pos, double maxSpeed, double maxAngularSpeed) {
        super(pos);
        this.maxSpeed = maxSpeed;
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public void update(double dt, Environment environment) {
        var desiredAngle = desiredDir.direction(); // todo was different way of calculating angle

        var angleDifference = CustomMath.wrapAngle(desiredAngle - angle);

        if (Math.abs(angleDifference) < maxAngularSpeed * dt) {
            angle = desiredAngle;
        } else {
            int moveTowards = Math.abs(desiredAngle - angle) < Math.PI ? 1 : -1;
            if (desiredAngle > angle) {
                angle += maxAngularSpeed * dt * moveTowards;
            } else {
                angle -= maxAngularSpeed * dt * moveTowards;
            }
        }

        angle = ((angle + Math.PI * 3) % (Math.PI * 2)) - Math.PI;

        pos = pos.add(Vector2d.fromDirection(angle).mul(desiredSpeed * maxSpeed * dt));
    }

    public void moveTo(Vector2d dir, double speed){
        desiredDir = dir;
        desiredSpeed = Math.max(0,Math.min(speed, 1));
    }

    /* public void setDesiredDirection(Vec2 dir) {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);


        GLFW.glfwGetCursorPos(Main.window, x, y);

        desiredDir = new Vec2(x.get(), y.get());

        desiredDir = desiredDir.mul(1 / 1000.0);
        desiredDir = new Vec2(desiredDir.x, 1 - desiredDir.y).plus(new Vec2(0.5, 0.5));



        Painter.square(desiredDir.x, desiredDir.y, 0.005);

        desiredDir = desiredDir.minus(pos).normalized();


        x.rewind();
        y.rewind();
    } */


    @Override
    public void draw() {
        glColor4d(0.8, 0.7, 0.7, 1);
        Painter.bird(pos.x, pos.y, 0.005, angle);
    }
}
