package graphics;

import math.CustomMath;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glColor4d;

@SuppressWarnings("unused")
public class Color {
    final double r;
    final double g;
    final double b;
    final double a;

    public Color(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
        a = 1;
    }

    public Color(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int hex){
        this.r = ((hex & 0xFF0000) >> 16) / 255.0;
        this.g = ((hex & 0xFF00) >> 8) / 255.0;
        this.b = (hex & 0xFF) / 255.0;
        this.a = 1;
    }

    public Color withAlpha(double alpha){
        return new Color(r, g, b, a * alpha);
    }

    public Color lerp(Color another, double val) {
        return new Color(
                CustomMath.lerp(this.r, another.r, val),
                CustomMath.lerp(this.g, another.g, val),
                CustomMath.lerp(this.b, another.b, val),
                CustomMath.lerp(this.a, another.a, val)
        );
    }

    public void glClearColor(){
        GL11.glClearColor((float) r,(float) g,(float) b,(float) a);
    }

    public void glColor(){
        glColor4d(r,g,b,a);
    }

    public void glColor(double alpha){
        glColor4d(r,g,b,a * alpha);
    }
}
