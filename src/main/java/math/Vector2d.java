package math;

import org.jetbrains.annotations.NotNull;

import static java.lang.Math.*;

@SuppressWarnings("unused")
public class Vector2d {
    public final double x;
    public final double y;

    public Vector2d() {
        x = 0;
        y = 0;
    }

    public static Vector2d fromDirection(double direction) {
        return new Vector2d(cos(direction), sin(direction));
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d normalized() {
        var dist = distance();
        if (dist < 0.00000001) return new Vector2d(1, 0);
        return this.mul(1.0 / distance());
    }

    public Vector2d mul(double val){
        return new Vector2d(x * val, y * val);
    }

    public Vector2d mul(Vector2d val){
        return new Vector2d(x * val.x, y * val.y);
    }

    public Vector2d div(double val) {
        return new Vector2d(x / val, y / val);
    }

    public Vector2d mapToActivation() {
        return new Vector2d(x / 2 + 0.5, y / 2 + 0.5);
    }

    public Vector2d add(Vector2d other){
        return new Vector2d(x + other.x, y + other.y);
    }

    public Vector2d sub(Vector2d other){
        return new Vector2d(x - other.x, y - other.y);
    }

    public double sqrDistance(){
        return x * x + y * y;
    }

    public double sqrDistance(Vector2d other){
        return this.sub(other).sqrDistance();
    }

    public double distance(){
        return sqrt(x * x + y * y);
    }

    public double distance(Vector2d other){
        return this.sub(other).distance();
    }

    public boolean inSquareBounds(Vector2d other, double r) {
        return (abs(y - other.y) < r) && (abs(x - other.x) < r);
    }

    @Override
    public String toString() {
        return "(" + x + ", "+ y + ")";
    }

    @Deprecated
    public double getDirection() {
        return atan2(x,y) + Math.PI;
    }

    public double direction(){
        return atan2(y,x);
    }

    public Vector2d projectOn(Vector2d other){
        var dp = dotProduct(other);

        return other.mul(dp).div(other.sqrDistance());
    }

    public double dotProduct(@NotNull Vector2d other) {
        return x * other.x + y * other.y;
    }

    public Vector2d rotate(double angle) {
        return Vector2d.fromDirection(this.direction() + angle).mul(distance());
    }

    public static Vector2d lerp(Vector2d v1, Vector2d v2, double val) {
        return new Vector2d(
                CustomMath.lerp(v1.x, v2.x, val),
                CustomMath.lerp(v1.y, v2.y, val)
        );
    }
}
