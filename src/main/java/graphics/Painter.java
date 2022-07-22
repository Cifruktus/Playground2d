package graphics;

import math.Vector2d;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("unused")
public class Painter {
    static int circleIterations = 10;
    static double arcIterations = 30;

    public static void square(Vector2d pos, double r) {
        square(pos.x, pos.y, r);
    }

    public static void square(double x, double y, double r) {
        glBegin(GL_POLYGON);
        glVertex2d(x - r, y - r);
        glVertex2d(x - r, y + r);
        glVertex2d(x + r, y + r);
        glVertex2d(x + r, y - r);
        glEnd();
    }

    public static void square(Vector2d start, Vector2d end) {
        glBegin(GL_POLYGON);
        glVertex2d(start.x, start.y);
        glVertex2d(end.x, start.y);
        glVertex2d(end.x, end.y);
        glVertex2d(start.x, end.y);
        glEnd();
    }

    public static void spark(double x, double y, double r) {
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x, y);

        glColor4d(0, 0, 0, 0);

        for (int i = 0; i <= circleIterations; i++) {
            glVertex2d(x + sin(i / (double) circleIterations * PI * 2) * r,
                    y + cos(i / (double) circleIterations * PI * 2) * r);
        }

        glEnd();
    }

    public static void arc(Vector2d origin, double r, double startAngle, double a) {
        glLineWidth(2);

        glBegin(GL_LINE_STRIP);

        for (int i = 0; i <= arcIterations; i++) {
            var angle = startAngle + a * i / arcIterations;
            glVertex2d(origin.x + r * sin(angle), origin.y + r * cos(angle));
        }

        glEnd();
    }

    public static void filledCircle(Vector2d origin, double r) {
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(origin.x, origin.y);

        for (int i = 0; i <= arcIterations; i++) {
            var angle = PI * 2.0 * i / arcIterations;
            glVertex2d(origin.x + r * sin(angle), origin.y + r * cos(angle));
        }

        glEnd();
    }

    public static void circle(Vector2d origin, double r) {
        glLineWidth(2);

        glBegin(GL_LINE_STRIP);

        for (int i = 0; i <= arcIterations; i++) {
            var angle = PI * 2.0 * i / arcIterations;
            glVertex2d(origin.x + r * sin(angle), origin.y + r * cos(angle));
        }

        glEnd();
    }

    public static void line(Vector2d origin, Vector2d end) {
        glLineWidth(2);

        glBegin(GL_LINES);
        glVertex2d(origin.x, origin.y);
        glVertex2d(end.x, end.y);
        glEnd();
    }

    public static void vector(Vector2d origin, Vector2d dir) {
        glLineWidth(2);

        var vectorEnd = origin.add(dir);

        glBegin(GL_LINES);
        glVertex2d(origin.x, origin.y);
        glVertex2d(vectorEnd.x, vectorEnd.y);
        glEnd();

        square(vectorEnd.x, vectorEnd.y, dir.distance() / 10);
    }

    public static void bird(double x, double y, double r, double angle) {
        glBegin(GL_TRIANGLES);


        glVertex2d(x + sin(angle) * r, y + cos(angle) * r);
        glVertex2d(x + sin(angle - PI + 0.4) * r, y + cos(angle - PI + 0.4) * r);
        glVertex2d(x + sin(angle - PI - 0.4) * r, y + cos(angle - PI - 0.4) * r);

        Vector2d offset = new Vector2d(x + sin(angle - PI) * r, y + cos(angle - PI) * r);

        glVertex2d(x, y);
        glVertex2d(offset.x + sin(angle - PI + 1) * r * 1.5, offset.y + cos(angle - PI + 1) * r * 1.5);
        glVertex2d(offset.x + sin(angle - PI - 1) * r * 1.5, offset.y + cos(angle - PI - 1) * r * 1.5);

        glEnd();
    }
}
