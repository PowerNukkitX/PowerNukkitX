package cn.nukkit.level.generator.densityfunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public final class CubicSpline<C> {

    private final ToDoubleFunction<C> coordinate;
    private final List<Point<C>> points;
    private final double minValue;
    private final double maxValue;

    private CubicSpline(ToDoubleFunction<C> coordinate, List<Point<C>> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("CubicSpline needs at least two points");
        }

        this.coordinate = coordinate;
        this.points = List.copyOf(points.stream().sorted(Comparator.comparingDouble(Point::location)).toList());

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (Point<C> point : this.points) {
            min = Math.min(min, point.value().minValue());
            max = Math.max(max, point.value().maxValue());
        }
        this.minValue = min;
        this.maxValue = max;
    }

    public static <C> Builder<C> builder(ToDoubleFunction<C> coordinate) {
        return new Builder<>(coordinate);
    }

    public double apply(C input) {
        double x = coordinate.applyAsDouble(input);
        Point<C> first = points.getFirst();
        if (x < first.location()) {
            return first.value().apply(input);
        }

        Point<C> last = points.getLast();
        if (x > last.location()) {
            return last.value().apply(input);
        }

        int low = 0;
        int high = points.size() - 1;
        while (high - low > 1) {
            int mid = (low + high) >>> 1;
            if (x < points.get(mid).location()) {
                high = mid;
            } else {
                low = mid;
            }
        }

        Point<C> p0 = points.get(low);
        Point<C> p1 = points.get(high);
        double y0 = p0.value().apply(input);
        double y1 = p1.value().apply(input);
        return hermite(x, p0.location(), y0, p0.derivative(), p1.location(), y1, p1.derivative());
    }

    public double minValue() {
        return minValue;
    }

    public double maxValue() {
        return maxValue;
    }

    private static double hermite(double x, double x0, double y0, double m0, double x1, double y1, double m1) {
        double t = (x - x0) / (x1 - x0);
        double t2 = t * t;
        double t3 = t2 * t;

        double h00 = 2 * t3 - 3 * t2 + 1;
        double h10 = t3 - 2 * t2 + t;
        double h01 = -2 * t3 + 3 * t2;
        double h11 = t3 - t2;

        return h00 * y0 + h10 * (x1 - x0) * m0 + h01 * y1 + h11 * (x1 - x0) * m1;
    }

    public interface Value<C> {
        double apply(C input);

        double minValue();

        double maxValue();
    }

    public record Point<C>(double location, Value<C> value, double derivative) {
    }

    public static final class Builder<C> {
        private final ToDoubleFunction<C> coordinate;
        private final List<Point<C>> points = new ArrayList<>();

        private Builder(ToDoubleFunction<C> coordinate) {
            this.coordinate = coordinate;
        }

        public Builder<C> addPoint(double location, double value, double derivative) {
            return addPoint(location, constant(value), derivative);
        }

        public Builder<C> addPoint(double location, Value<C> value, double derivative) {
            points.add(new Point<>(location, value, derivative));
            return this;
        }

        public CubicSpline<C> build() {
            return new CubicSpline<>(coordinate, points);
        }
    }

    public static <C> Value<C> constant(double value) {
        return new ConstantValue<>(value);
    }

    private record ConstantValue<C>(double value) implements Value<C> {
        @Override
        public double apply(C input) {
            return value;
        }

        @Override
        public double minValue() {
            return value;
        }

        @Override
        public double maxValue() {
            return value;
        }
    }
}
