package cn.nukkit.level.generator.densityfunction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public final class CubicSpline<C> {

    private final ToDoubleFunction<C> coordinate;
    private final double[] locations;
    private final Value<C>[] values;
    private final double[] derivatives;
    private final double minValue;
    private final double maxValue;

    @SuppressWarnings("unchecked")
    private CubicSpline(ToDoubleFunction<C> coordinate, List<Point<C>> points) {
        if (points.size() < 2) {
            throw new IllegalArgumentException("CubicSpline needs at least two points");
        }

        this.coordinate = coordinate;
        List<Point<C>> sortedPoints = List.copyOf(points.stream().sorted(Comparator.comparingDouble(Point::location)).toList());
        int pointCount = sortedPoints.size();
        this.locations = new double[pointCount];
        this.values = (Value<C>[]) new Value<?>[pointCount];
        this.derivatives = new double[pointCount];

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < pointCount; i++) {
            Point<C> point = sortedPoints.get(i);
            Value<C> value = point.value();
            this.locations[i] = point.location();
            this.values[i] = value;
            this.derivatives[i] = point.derivative();
            min = Math.min(min, value.minValue());
            max = Math.max(max, value.maxValue());
        }
        this.minValue = min;
        this.maxValue = max;
    }

    public static <C> Builder<C> builder(ToDoubleFunction<C> coordinate) {
        return new Builder<>(coordinate);
    }

    public double apply(C input) {
        double x = coordinate.applyAsDouble(input);
        int range = findRangeForLocation(locations, x);
        if (range < 0) {
            return values[0].apply(input);
        }

        int last = locations.length - 1;
        if (range == last) {
            return values[last].apply(input);
        }

        double loc0 = locations[range];
        double loc1 = locations[range + 1];
        double locDist = loc1 - loc0;
        double k = (x - loc0) / locDist;
        double y0 = values[range].apply(input);
        double y1 = values[range + 1].apply(input);
        double yDist = y1 - y0;
        double p = derivatives[range] * locDist - yDist;
        double q = -derivatives[range + 1] * locDist + yDist;
        return y0 + k * yDist + k * (1.0 - k) * (p + k * (q - p));
    }

    public double minValue() {
        return minValue;
    }

    public double maxValue() {
        return maxValue;
    }

    private static int findRangeForLocation(double[] locations, double x) {
        int min = 0;
        int length = locations.length;

        while (length > 0) {
            int half = length >>> 1;
            int mid = min + half;
            if (x < locations[mid]) {
                length = half;
            } else {
                min = mid + 1;
                length -= half + 1;
            }
        }

        return min - 1;
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
