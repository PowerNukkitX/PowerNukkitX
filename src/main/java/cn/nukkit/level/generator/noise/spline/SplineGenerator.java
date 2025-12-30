package cn.nukkit.level.generator.noise.spline;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class SplineGenerator {

    public interface IEvaluator {
        double evaluate(Map<String, Double> parameters);
    }

    public static class Point {
        public final double location;
        public final IEvaluator value;
        public final double derivative;

        public Point(double location, IEvaluator value, double derivative) {
            this.location = location;
            this.value = value;
            this.derivative = derivative;
        }

        public Point(double location, double value, double derivative) {
            this(location, new StaticValue(value), derivative);
        }
    }

    public static class StaticValue implements IEvaluator {
        private final double value;
        public StaticValue(double value) {
            this.value = value;
        }

        @Override
        public double evaluate(Map<String, Double> parameters) {
            return this.value;
        }
    }

    public static class Spline implements IEvaluator {
        private final List<Point> points;
        private final String coordinate;

        public Spline(String coordinate, List<Point> points) {
            if (points.size() < 2) {
                throw new IllegalArgumentException("Spline needs at least two points");
            }
            this.points = points;
            this.coordinate = coordinate;
        }

        @Override
        public double evaluate(Map<String, Double> parameters) {
            double input = parameters.getOrDefault(this.coordinate, 0.0);

            for (int i = 0; i < points.size() - 1; i++) {
                Point p0 = points.get(i);
                Point p1 = points.get(i + 1);

                if (input >= p0.location && input <= p1.location) {
                    double y0 = p0.value.evaluate(parameters);
                    double y1 = p1.value.evaluate(parameters);
                    return hermite(input, p0.location, y0, p0.derivative, p1.location, y1, p1.derivative);
                }
            }

            if (input < points.getFirst().location) {
                return points.getFirst().value.evaluate(parameters);
            } else {
                return points.getLast().value.evaluate(parameters);
            }
        }

        private double hermite(double x, double x0, double y0, double m0, double x1, double y1, double m1) {
            double t = (x - x0) / (x1 - x0);
            double t2 = t * t;
            double t3 = t2 * t;

            double h00 = 2 * t3 - 3 * t2 + 1;
            double h10 = t3 - 2 * t2 + t;
            double h01 = -2 * t3 + 3 * t2;
            double h11 = t3 - t2;

            return h00 * y0 + h10 * (x1 - x0) * m0 + h01 * y1 + h11 * (x1 - x0) * m1;
        }
    }
}