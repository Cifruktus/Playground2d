package math;

import java.util.Arrays;

import static java.lang.Math.*;

@SuppressWarnings("unused")
public class CustomMath {
    public static double sigmoid(double x) {
        return 1 / (1 + pow(E, -x));
    }

    public static double sigmoidDerivative(double x) {
        double sigmoid = sigmoid(x);
        return sigmoid * (1 - sigmoid);
    }

    public static void fill2dArray(double[][] input, double val){
        for (double[] doubles : input) {
            Arrays.fill(doubles, val);
        }
    }

    public static void fill3dArray(double[][][] input, double val){
        for (double[][] doubles : input) {
            fill2dArray(doubles, val);
        }
    }

    public static void multiplyArray(double[] input, double val) {
            for (int i = 0; i < input.length; i++) {
                input[i] *= val;
            }
    }

    public static void divideArray(double[] input, double val) {
        for (int i = 0; i < input.length; i++) {
            input[i] /= val;
        }
    }

    public static void addToArray(double[] input, double val) {
        for (int i = 0; i < input.length; i++) {
            input[i] += val;
        }
    }

    public static double[] sum(double[] input, double[] another) {
        assert (input.length == another.length);

        double[] answer = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            answer[i] = input[i] + another[i];
        }

        return answer;
    }

    public static double[] sub(double[] input, double[] another) {
        assert (input.length == another.length);

        double[] answer = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            answer[i] = input[i] - another[i];
        }

        return answer;
    }

    public static void normalizeArray(double[] input, boolean removeOffset) {
        double max = - 1e10;
        double min = 1e10;

        for (double val : input) {
            max = max(max, val);
            min = min(min, val);
        }

        if (removeOffset) {
            double division = max - min;
            for (int i = 0; i < input.length; i++) {
                input[i] -= min;
                input[i] /= division;
            }
        } else {
            for (int i = 0; i < input.length; i++) {
                input[i] /= max;
            }
        }
    }

    public static double maxInArray(double[] input){
        var answer = Double.NEGATIVE_INFINITY;
        for (var val : input) {
            answer = Math.max(answer, val);
        }
        return answer;
    }

    public static double minInArray(double[] input){
        var answer = Double.POSITIVE_INFINITY;
        for (var val : input) {
            answer = Math.min(answer, val);
        }
        return answer;
    }

    public static double average(double[] input){
        var answer = 0.0;
        for (var val : input) {
            answer += val;
        }
        return answer / input.length;
    }

    public static double[] concatArray(double[][] input){
        int length = 0;

        for (int i = 0; i < input.length; i++) {
            length += input[i].length;
        }

        var answer = new double[length];

        int pointer = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                answer[pointer] = input[i][j];
                pointer++;
            }
        }
        return answer;
    }


    public static  double[][] copy2dArray(double[][] input){
        double[][] answer = new double[input.length][];

        for (int i = 0; i < answer.length; i++) {
            answer[i] = Arrays.copyOf(input[i], input[i].length);
        }

        return answer;
    }

    public static  double[][][] copy3dArray(double[][][] input){
        double[][][] answer = new double[input.length][][];

        for (int i = 0; i < answer.length; i++) {
            answer[i] = copy2dArray(input[i]);
        }

        return answer;
    }

    public static double wrapAngle(double angle){
        while (angle > PI) angle -= 2 * PI;
        while (angle < -PI) angle += 2 * PI;
        return angle;
    }

    public static double lerp(double v, double v1, double value) {
        return v * (1 - value) + v1 * value;
    }
}
