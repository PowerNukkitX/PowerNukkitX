package cn.nukkit.level.generator.noise.spline;

import java.util.List;
import java.util.Arrays;

public class Spline {

    public static class Point {
        double location;
        double derivative;
        Object value;

        public Point(double location, double value, double derivative) {
            this.location = location;
            this.value = value;
            this.derivative = derivative;
        }

        public Point(double location, Spline spline, double derivative) {
            this.location = location;
            this.value = spline;
            this.derivative = derivative;
        }

        double getValue() {
            if (value instanceof Double) {
                return (Double) value;
            } else {
                throw new IllegalStateException("Unexpected Spline Type");
            }
        }
    }

    private final List<Point> points;

    public Spline(List<Point> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("Spline needs at least two points");
        }
        this.points = points;
    }

    public double evaluate(double input) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p0 = points.get(i);
            Point p1 = points.get(i + 1);

            if (input >= p0.location && input <= p1.location) {
                return hermite(
                        input,
                        p0.location, p0.getValue(), p0.derivative,
                        p1.location, p1.getValue(), p1.derivative
                );
            }
        }

        if (input < points.getFirst().location) {
            return points.getFirst().getValue();
        }
        return points.getLast().getValue();
    }


    public double evaluate(double continental, double erosion, double pv) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p0 = points.get(i);
            Point p1 = points.get(i + 1);

            if (continental >= p0.location && continental <= p1.location) {
                double y0, y1;
                if (p0.value instanceof Double) {
                    y0 = (Double) p0.value;
                } else {
                    y0 = ((Spline) p0.value).evaluate(erosion, pv);
                }
                if (p1.value instanceof Double) {
                    y1 = (Double) p1.value;
                } else {
                    y1 = ((Spline) p1.value).evaluate(erosion, pv);
                }

                return hermite(
                        continental,
                        p0.location, y0, p0.derivative,
                        p1.location, y1, p1.derivative
                );
            }
        }

        if (continental < points.getFirst().location) {
            if (points.getFirst().value instanceof Double) {
                return (Double) points.getFirst().value;
            } else {
                return ((Spline) points.getFirst().value).evaluate(erosion, pv);
            }
        }
        if (points.getLast().value instanceof Double) {
            return (Double) points.getLast().value;
        } else {
            return ((Spline) points.getLast().value).evaluate(erosion, pv);
        }
    }


    public double evaluate(double erosion, double pv) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point p0 = points.get(i);
            Point p1 = points.get(i + 1);

            if (erosion >= p0.location && erosion <= p1.location) {
                double y0, y1;
                if (p0.value instanceof Double) {
                    y0 = (Double) p0.value;
                } else {
                    y0 = ((Spline) p0.value).evaluate(pv);
                }
                if (p1.value instanceof Double) {
                    y1 = (Double) p1.value;
                } else {
                    y1 = ((Spline) p1.value).evaluate(pv);
                }

                return hermite(
                        erosion,
                        p0.location, y0, p0.derivative,
                        p1.location, y1, p1.derivative
                );
            }
        }

        if (erosion < points.getFirst().location) {
            if (points.getFirst().value instanceof Double) {
                return (Double) points.getFirst().value;
            } else {
                return ((Spline) points.getFirst().value).evaluate(pv);
            }
        }
        if (points.getLast().value instanceof Double) {
            return (Double) points.getLast().value;
        } else {
            return ((Spline) points.getLast().value).evaluate(pv);
        }
    }

    private double hermite(
            double x,
            double x0, double y0, double m0,
            double x1, double y1, double m1
    ) {
        double t = (x - x0) / (x1 - x0);

        double h00 = 2 * t * t * t - 3 * t * t + 1;
        double h10 = t * t * t - 2 * t * t + t;
        double h01 = -2 * t * t * t + 3 * t * t;
        double h11 = t * t * t - t * t;

        return h00 * y0
                + h10 * (x1 - x0) * m0
                + h01 * y1
                + h11 * (x1 - x0) * m1;
    }

    private static final Spline CACHED_DEPTH_SPLINE;

    static {
        CACHED_DEPTH_SPLINE = new Spline(Arrays.asList(
                new Point(-1.1, 0.044, 0.0),
                new Point(-1.02, -0.2222, 0.0),
                new Point(-0.51, -0.2222, 0.0),
                new Point(-0.44, -0.12, 0.0),
                new Point(-0.18, -0.12, 0.0),
                new Point(-0.16, new Spline(Arrays.asList(
                        new Point(-0.85, new Spline(Arrays.asList(
                                new Point(-1.0, -0.08880186, 0.38940096),
                                new Point(1.0, 0.69000006, 0.38940096)
                        )), 0.0),
                        new Point(-0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.115760356, 0.37788022),
                                new Point(1.0, 0.6400001, 0.37788022)
                        )), 0.0),
                        new Point(-0.4, new Spline(Arrays.asList(
                                new Point(-1.0, -0.2222, 0.0),
                                new Point(-0.75, -0.2222, 0.0),
                                new Point(-0.65, 0.0, 0.0),
                                new Point(0.5954547, 2.9802322E-8, 0.0),
                                new Point(0.6054547, 2.9802322E-8, 0.2534563),
                                new Point(1.0, 0.100000024, 0.2534563)
                        )), 0.0),
                        new Point(-0.35, new Spline(Arrays.asList(
                                new Point(-1.0, -0.3, 0.5),
                                new Point(-0.4, 0.05, 0.0),
                                new Point(0.0, 0.05, 0.0),
                                new Point(0.4, 0.05, 0.0),
                                new Point(1.0, 0.060000002, 0.007000001)
                        )), 0.0),
                        new Point(-0.1, new Spline(Arrays.asList(
                                new Point(-1.0, -0.15, 0.5),
                                new Point(-0.4, 0.0, 0.0),
                                new Point(0.0, 0.0, 0.0),
                                new Point(0.4, 0.05, 0.1),
                                new Point(1.0, 0.060000002, 0.007000001)
                        )), 0.0),
                        new Point(0.2, new Spline(Arrays.asList(
                                new Point(-1.0, -0.15, 0.5),
                                new Point(-0.4, 0.0, 0.0),
                                new Point(0.0, 0.0, 0.0),
                                new Point(0.4, 0.0, 0.0),
                                new Point(1.0, 0.0, 0.0)
                        )), 0.0),
                        new Point(0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.02, 0.0),
                                new Point(-0.4, -0.03, 0.0),
                                new Point(0.0, -0.03, 0.0),
                                new Point(0.4, 0.0, 0.06),
                                new Point(1.0, 0.0, 0.0)
                        )), 0.0)
                )), 0.0),
                new Point(-0.15, new Spline(Arrays.asList(
                        new Point(-0.85, new Spline(Arrays.asList(
                                new Point(-1.0, -0.08880186, 0.38940096),
                                new Point(1.0, 0.69000006, 0.38940096)
                        )), 0.0),

                        new Point(-0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.115760356, 0.37788022),
                                new Point(1.0, 0.6400001, 0.37788022)
                        )), 0.0),

                        new Point(-0.4, new Spline(Arrays.asList(
                                new Point(-1.0, -0.2222, 0.0),
                                new Point(-0.75, -0.2222, 0.0),
                                new Point(-0.65, 0.0, 0.0),
                                new Point(0.5954547, 2.9802322E-8, 0.0),
                                new Point(0.6054547, 2.9802322E-8, 0.2534563),
                                new Point(1.0, 0.100000024, 0.2534563)
                        )), 0.0),

                        new Point(-0.35, new Spline(Arrays.asList(
                                new Point(-1.0, -0.3, 0.5),
                                new Point(-0.4, 0.05, 0.0),
                                new Point(0.0, 0.05, 0.0),
                                new Point(0.4, 0.05, 0.0),
                                new Point(1.0, 0.060000002, 0.007000001)
                        )), 0.0),

                        new Point(-0.1, new Spline(Arrays.asList(
                                new Point(-1.0, -0.15, 0.5),
                                new Point(-0.4, 0.0, 0.0),
                                new Point(0.0, 0.0, 0.0),
                                new Point(0.4, 0.05, 0.1),
                                new Point(1.0, 0.060000002, 0.007000001)
                        )), 0.0),

                        new Point(0.2, new Spline(Arrays.asList(
                                new Point(-1.0, -0.15, 0.5),
                                new Point(-0.4, 0.0, 0.0),
                                new Point(0.0, 0.0, 0.0),
                                new Point(0.4, 0.0, 0.0),
                                new Point(1.0, 0.0, 0.0)
                        )), 0.0),

                        new Point(0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.02, 0.0),
                                new Point(-0.4, -0.03, 0.0),
                                new Point(0.0, -0.03, 0.0),
                                new Point(0.4, 0.0, 0.06),
                                new Point(1.0, 0.0, 0.0)
                        )), 0.0)
                )), 0.0),
                new Point(-0.1, new Spline(Arrays.asList(
                        new Point(-0.85, new Spline(Arrays.asList(
                                new Point(-1.0, -0.08880186, 0.38940096),
                                new Point(1.0, 0.69000006, 0.38940096)
                        )), 0.0),

                        new Point(-0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.115760356, 0.37788022),
                                new Point(1.0, 0.6400001, 0.37788022)
                        )), 0.0),

                        new Point(-0.4, new Spline(Arrays.asList(
                                new Point(-1.0, -0.2222, 0.0),
                                new Point(-0.75, -0.2222, 0.0),
                                new Point(-0.65, 0.0, 0.0),
                                new Point(0.5954547, 2.9802322E-8, 0.0),
                                new Point(0.6054547, 2.9802322E-8, 0.2534563),
                                new Point(1.0, 0.100000024, 0.2534563)
                        )), 0.0),

                        new Point(-0.35, new Spline(Arrays.asList(
                                new Point(-1.0, -0.25, 0.5),
                                new Point(-0.4, 0.05, 0.0),
                                new Point(0.0, 0.05, 0.0),
                                new Point(0.4, 0.05, 0.0),
                                new Point(1.0, 0.060000002, 0.007000001)
                        )), 0.0),

                        new Point(-0.1, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.5),
                                new Point(-0.4, 0.001, 0.01),
                                new Point(0.0, 0.003, 0.01),
                                new Point(0.4, 0.05, 0.094000004),
                                new Point(1.0, 0.060000002, 0.007000001)
                        )), 0.0),

                        new Point(0.2, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.02, 0.0),
                                new Point(-0.4, -0.03, 0.0),
                                new Point(0.0, -0.03, 0.0),
                                new Point(0.4, 0.03, 0.12),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0)
                )), 0.0),
                new Point(0.25, new Spline(Arrays.asList(
                        new Point(-0.85, new Spline(Arrays.asList(
                                new Point(-1.0, 0.20235021, 0.0),
                                new Point(0.0, 0.7161751, 0.5138249),
                                new Point(1.0, 1.23, 0.5138249)
                        )), 0.0),

                        new Point(-0.7, new Spline(Arrays.asList(
                                new Point(-1.0, 0.2, 0.0),
                                new Point(0.0, 0.44682026, 0.43317974),
                                new Point(1.0, 0.88, 0.43317974)
                        )), 0.0),

                        new Point(-0.4, new Spline(Arrays.asList(
                                new Point(-1.0, 0.2, 0.0),
                                new Point(0.0, 0.30829495, 0.3917051),
                                new Point(1.0, 0.70000005, 0.3917051)
                        )), 0.0),

                        new Point(-0.35, new Spline(Arrays.asList(
                                new Point(-1.0, -0.25, 0.5),
                                new Point(-0.4, 0.35, 0.0),
                                new Point(0.0, 0.35, 0.0),
                                new Point(0.4, 0.35, 0.0),
                                new Point(1.0, 0.42000002, 0.049000014)
                        )), 0.0),

                        new Point(-0.1, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.5),
                                new Point(-0.4, 0.0069999998, 0.07),
                                new Point(0.0, 0.021, 0.07),
                                new Point(0.4, 0.35, 0.658),
                                new Point(1.0, 0.42000002, 0.049000014)
                        )), 0.0),

                        new Point(0.2, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.4, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.45, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.0),
                                new Point(-0.4, new Spline(Arrays.asList(
                                        new Point(-1.0, -0.1, 0.5),
                                        new Point(-0.4, 0.01, 0.0),
                                        new Point(0.0, 0.01, 0.0),
                                        new Point(0.4, 0.03, 0.04),
                                        new Point(1.0, 0.1, 0.049)
                                )), 0.0),
                                new Point(0.0, 0.17, 0.0)
                        )), 0.0),

                        new Point(0.55, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.0),
                                new Point(-0.4, new Spline(Arrays.asList(
                                        new Point(-1.0, -0.1, 0.5),
                                        new Point(-0.4, 0.01, 0.0),
                                        new Point(0.0, 0.01, 0.0),
                                        new Point(0.4, 0.03, 0.04),
                                        new Point(1.0, 0.1, 0.049)
                                )), 0.0),
                                new Point(0.0, 0.17, 0.0)
                        )), 0.0),

                        new Point(0.58, new Spline(Arrays.asList(
                                new Point(-1.0, -0.1, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.02, 0.0),
                                new Point(-0.4, -0.03, 0.0),
                                new Point(0.0, -0.03, 0.0),
                                new Point(0.4, 0.03, 0.12),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0)
                )), 0.0),
                new Point(1.0, new Spline(Arrays.asList(
                        new Point(-0.85, new Spline(Arrays.asList(
                                new Point(-1.0, 0.34792626, 0.0),
                                new Point(0.0, 0.9239631, 0.5760369),
                                new Point(1.0, 1.5, 0.5760369)
                        )), 0.0),

                        new Point(-0.7, new Spline(Arrays.asList(
                                new Point(-1.0, 0.2, 0.0),
                                new Point(0.0, 0.5391705, 0.4608295),
                                new Point(1.0, 1.0, 0.4608295)
                        )), 0.0),

                        new Point(-0.4, new Spline(Arrays.asList(
                                new Point(-1.0, 0.2, 0.0),
                                new Point(0.0, 0.5391705, 0.4608295),
                                new Point(1.0, 1.0, 0.4608295)
                        )), 0.0),

                        new Point(-0.35, new Spline(Arrays.asList(
                                new Point(-1.0, -0.2, 0.5),
                                new Point(-0.4, 0.5, 0.0),
                                new Point(0.0, 0.5, 0.0),
                                new Point(0.4, 0.5, 0.0),
                                new Point(1.0, 0.6, 0.070000015)
                        )), 0.0),

                        new Point(-0.1, new Spline(Arrays.asList(
                                new Point(-1.0, -0.05, 0.5),
                                new Point(-0.4, 0.01, 0.099999994),
                                new Point(0.0, 0.03, 0.099999994),
                                new Point(0.4, 0.5, 0.94),
                                new Point(1.0, 0.6, 0.070000015)
                        )), 0.0),

                        new Point(0.2, new Spline(Arrays.asList(
                                new Point(-1.0, -0.05, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.4, new Spline(Arrays.asList(
                                new Point(-1.0, -0.05, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.45, new Spline(Arrays.asList(
                                new Point(-1.0, -0.05, 0.0),
                                new Point(-0.4, new Spline(Arrays.asList(
                                        new Point(-1.0, -0.05, 0.5),
                                        new Point(-0.4, 0.01, 0.0),
                                        new Point(0.0, 0.01, 0.0),
                                        new Point(0.4, 0.03, 0.04),
                                        new Point(1.0, 0.1, 0.049)
                                )), 0.0),
                                new Point(0.0, 0.17, 0.0)
                        )), 0.0),

                        new Point(0.55, new Spline(Arrays.asList(
                                new Point(-1.0, -0.05, 0.0),
                                new Point(-0.4, new Spline(Arrays.asList(
                                        new Point(-1.0, -0.05, 0.5),
                                        new Point(-0.4, 0.01, 0.0),
                                        new Point(0.0, 0.01, 0.0),
                                        new Point(0.4, 0.03, 0.04),
                                        new Point(1.0, 0.1, 0.049)
                                )), 0.0),
                                new Point(0.0, 0.17, 0.0)
                        )), 0.0),

                        new Point(0.58, new Spline(Arrays.asList(
                                new Point(-1.0, -0.05, 0.5),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0),

                        new Point(0.7, new Spline(Arrays.asList(
                                new Point(-1.0, -0.02, 0.015),
                                new Point(-0.4, 0.01, 0.0),
                                new Point(0.0, 0.01, 0.0),
                                new Point(0.4, 0.03, 0.04),
                                new Point(1.0, 0.1, 0.049)
                        )), 0.0)
                )), 0.0)
        ));
    }

    public static float getDepth(float continental, float erosion, float pv) {
        return -0.5037500262260437f + (float) CACHED_DEPTH_SPLINE.evaluate(continental, erosion, pv);
    }
}