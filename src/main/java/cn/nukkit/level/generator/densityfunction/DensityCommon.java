package cn.nukkit.level.generator.densityfunction;

import cn.nukkit.level.generator.noise.minecraft.noise.NormalNoise;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.RandomSourceProvider;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common Density functions for PowerNukkitX
 * @author Buddelbubi
 * @since 2026/04/02
 */
public final class DensityCommon {

    private DensityCommon() {
    }

    public static DensityFunction interpolated(DensityFunction function) {
        return Marker.create(Marker.Type.INTERPOLATED, function);
    }

    public static DensityFunction flatCache(DensityFunction function) {
        return Marker.create(Marker.Type.FLAT_CACHE, function);
    }

    public static DensityFunction cache2d(DensityFunction function) {
        return Marker.create(Marker.Type.CACHE_2D, function);
    }

    public static DensityFunction cacheOnce(DensityFunction function) {
        return Marker.create(Marker.Type.CACHE_ONCE, function);
    }

    public static DensityFunction noise(NormalNoise noise) {
        return new Noise(new DensityFunction.NoiseHolder(noise), 1.0, 1.0);
    }

    public static DensityFunction noise(NormalNoise noise, double xzScale, double yScale) {
        return new Noise(new DensityFunction.NoiseHolder(noise), xzScale, yScale);
    }

    public static DensityFunction noise(DensityFunction.NoiseHolder noise, double xzScale, double yScale) {
        return new Noise(noise, xzScale, yScale);
    }

    public static DensityFunction mappedNoise(NormalNoise noise, double min, double max) {
        return mapFromUnitTo(noise(noise, 1.0, 1.0), min, max);
    }

    public static DensityFunction mappedNoise(NormalNoise noise, double xzScale, double yScale, double min, double max) {
        return mapFromUnitTo(noise(noise, xzScale, yScale), min, max);
    }

    public static DensityFunction weirdScaledSampler(
            DensityFunction input,
            NormalNoise noise,
            WeirdScaledSampler.RarityValueMapper rarityValueMapper
    ) {
        return new WeirdScaledSampler(input, new DensityFunction.NoiseHolder(noise), rarityValueMapper);
    }

    public static DensityFunction shiftA(NormalNoise noise) {
        return new ShiftA(new DensityFunction.NoiseHolder(noise));
    }

    public static DensityFunction shiftB(NormalNoise noise) {
        return new ShiftB(new DensityFunction.NoiseHolder(noise));
    }

    public static DensityFunction shift(NormalNoise noise) {
        return new Shift(new DensityFunction.NoiseHolder(noise));
    }

    public static DensityFunction blendAlpha() {
        return BlendAlpha.INSTANCE;
    }

    public static DensityFunction blendOffset() {
        return BlendOffset.INSTANCE;
    }

    public static DensityFunction blendDensity(DensityFunction input) {
        return input;
    }

    private static DensityFunction mapFromUnitTo(DensityFunction input, double min, double max) {
        double midpoint = (min + max) * 0.5;
        double scale = (max - min) * 0.5;
        return add(constant(midpoint), mul(constant(scale), input));
    }

    public static DensityFunction rangeChoice(
            DensityFunction input,
            double minInclusive,
            double maxExclusive,
            DensityFunction whenInRange,
            DensityFunction whenOutOfRange
    ) {
        return new RangeChoice(input, minInclusive, maxExclusive, whenInRange, whenOutOfRange);
    }

    public static DensityFunction add(DensityFunction f1, DensityFunction f2) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.ADD, f1, f2);
    }

    public static DensityFunction mul(DensityFunction f1, DensityFunction f2) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.MUL, f1, f2);
    }

    public static DensityFunction min(DensityFunction f1, DensityFunction f2) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.MIN, f1, f2);
    }

    public static DensityFunction max(DensityFunction f1, DensityFunction f2) {
        return TwoArgumentSimpleFunction.create(TwoArgumentSimpleFunction.Type.MAX, f1, f2);
    }

    public static DensityFunction zero() {
        return Constant.ZERO;
    }

    public static DensityFunction constant(double value) {
        return new Constant(value);
    }

    public static DensityFunction yClampedGradient(int fromY, int toY, double fromValue, double toValue) {
        return new YClampedGradient(fromY, toY, fromValue, toValue);
    }

    public static DensityFunction map(DensityFunction function, Mapped.Type type) {
        return Mapped.create(type, function);
    }

    public static DensityFunction lerp(DensityFunction factor, double first, DensityFunction second) {
        return add(mul(factor, add(second, constant(-first))), constant(first));
    }

    public static DensityFunction spline(CubicSpline<Spline.Point> spline) {
        return new Spline(spline);
    }

    public static DensityFunction spline(DensityFunction coordinate, SplinePoint... points) {
        CubicSpline.Builder<Spline.Point> builder =
                CubicSpline.builder(point -> coordinate.compute(point.context()));
        for (SplinePoint point : points) {
            if (point.value instanceof DensityFunction function) {
                builder.addPoint(point.location, new Spline.Coordinate(function), point.derivative);
            } else {
                builder.addPoint(point.location, ((Number) point.value).doubleValue(), point.derivative);
            }
        }
        return spline(builder.build());
    }

    public static SplinePoint p(double location, double value, double derivative) {
        return new SplinePoint(location, value, derivative);
    }

    public static SplinePoint p(double location, DensityFunction value, double derivative) {
        return new SplinePoint(location, value, derivative);
    }

    record Constant(double value) implements DensityFunction.SimpleFunction {
        private static final Constant ZERO = new Constant(0.0);

        @Override
        public double compute(FunctionContext context) {
            return value;
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            Arrays.fill(output, value);
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

    public record Clamp(DensityFunction input, double min, double max) implements PureTransformer {
        @Override
        public double transform(double inputValue) {
            return NukkitMath.clamp(inputValue, min, max);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new Clamp(input.mapAll(visitor), min, max));
        }

        @Override
        public double minValue() {
            return min;
        }

        @Override
        public double maxValue() {
            return max;
        }
    }

    public record Noise(NoiseHolder noise, double xzScale, double yScale) implements DensityFunction {
        @Override
        public double compute(FunctionContext context) {
            return noise.getValue(context.blockX() * xzScale, context.blockY() * yScale, context.blockZ() * xzScale);
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new Noise(visitor.visitNoise(noise), xzScale, yScale));
        }

        @Override
        public double minValue() {
            return -maxValue();
        }

        @Override
        public double maxValue() {
            return noise.maxValue();
        }
    }

    public record RangeChoice(
            DensityFunction input,
            double minInclusive,
            double maxExclusive,
            DensityFunction whenInRange,
            DensityFunction whenOutOfRange
    ) implements DensityFunction {
        @Override
        public double compute(FunctionContext context) {
            double inputValue = input.compute(context);
            return inputValue >= minInclusive && inputValue < maxExclusive
                    ? whenInRange.compute(context)
                    : whenOutOfRange.compute(context);
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            input.fillArray(output, contextProvider);
            for (int i = 0; i < output.length; i++) {
                double value = output[i];
                output[i] = value >= minInclusive && value < maxExclusive
                        ? whenInRange.compute(contextProvider.forIndex(i))
                        : whenOutOfRange.compute(contextProvider.forIndex(i));
            }
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new RangeChoice(
                    input.mapAll(visitor),
                    minInclusive,
                    maxExclusive,
                    whenInRange.mapAll(visitor),
                    whenOutOfRange.mapAll(visitor)
            ));
        }

        @Override
        public double minValue() {
            return Math.min(whenInRange.minValue(), whenOutOfRange.minValue());
        }

        @Override
        public double maxValue() {
            return Math.max(whenInRange.maxValue(), whenOutOfRange.maxValue());
        }
    }

    public record WeirdScaledSampler(
            DensityFunction input,
            DensityFunction.NoiseHolder noise,
            RarityValueMapper rarityValueMapper
    ) implements TransformerWithContext {
        @Override
        public double transform(FunctionContext context, double inputValue) {
            double rarity = rarityValueMapper.mapper.apply(inputValue);
            return rarity * Math.abs(noise.getValue(
                    context.blockX() / rarity,
                    context.blockY() / rarity,
                    context.blockZ() / rarity
            ));
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new WeirdScaledSampler(
                    input.mapAll(visitor),
                    visitor.visitNoise(noise),
                    rarityValueMapper
            ));
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return rarityValueMapper.maxRarity * noise.maxValue();
        }

        public enum RarityValueMapper {
            TYPE1(WeirdScaledSampler::getSpaghettiRarity3D, 2.0),
            TYPE2(WeirdScaledSampler::getSpaghettiRarity2D, 3.0);

            private final Mapper mapper;
            private final double maxRarity;

            RarityValueMapper(Mapper mapper, double maxRarity) {
                this.mapper = mapper;
                this.maxRarity = maxRarity;
            }
        }

        @FunctionalInterface
        private interface Mapper {
            double apply(double value);
        }

        private static double getSpaghettiRarity2D(double value) {
            if (value < -0.75) {
                return 0.5;
            } else if (value < -0.5) {
                return 0.75;
            } else if (value < 0.5) {
                return 1.0;
            } else if (value < 0.75) {
                return 2.0;
            } else {
                return 3.0;
            }
        }

        private static double getSpaghettiRarity3D(double value) {
            if (value < -0.5) {
                return 0.75;
            } else if (value < 0.0) {
                return 1.0;
            } else if (value < 0.5) {
                return 1.5;
            } else {
                return 2.0;
            }
        }
    }

    public record FindTopSurface(DensityFunction density, DensityFunction upperBound, int lowerBound, int cellHeight) implements DensityFunction {
        private static final ThreadLocal<MutableFunctionContext> SURFACE_CONTEXT =
                ThreadLocal.withInitial(MutableFunctionContext::new);

        @Override
        public double compute(FunctionContext context) {
            int topY = NukkitMath.floorDouble(upperBound.compute(context) / cellHeight) * cellHeight;
            if (topY <= lowerBound) {
                return lowerBound;
            }

            MutableFunctionContext surfaceContext = SURFACE_CONTEXT.get()
                    .set(context.blockX(), topY, context.blockZ());
            for (int blockY = topY; blockY >= lowerBound; blockY -= cellHeight) {
                surfaceContext.set(context.blockX(), blockY, context.blockZ());
                if (density.compute(surfaceContext) > 0.0) {
                    return blockY;
                }
            }
            return lowerBound;
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new FindTopSurface(
                    density.mapAll(visitor),
                    upperBound.mapAll(visitor),
                    lowerBound,
                    cellHeight
            ));
        }

        @Override
        public double minValue() {
            return lowerBound;
        }

        @Override
        public double maxValue() {
            return Math.max(lowerBound, upperBound.maxValue());
        }
    }

    public record YClampedGradient(int fromY, int toY, double fromValue, double toValue) implements DensityFunction.SimpleFunction {
        @Override
        public double compute(FunctionContext context) {
            return clampedMap(context.blockY(), fromY, toY, fromValue, toValue);
        }

        @Override
        public double minValue() {
            return Math.min(fromValue, toValue);
        }

        @Override
        public double maxValue() {
            return Math.max(fromValue, toValue);
        }
    }

    enum BlendAlpha implements DensityFunction.SimpleFunction {
        INSTANCE;

        @Override
        public double compute(FunctionContext context) {
            return 1.0;
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            Arrays.fill(output, 1.0);
        }

        @Override
        public double minValue() {
            return 1.0;
        }

        @Override
        public double maxValue() {
            return 1.0;
        }
    }

    enum BlendOffset implements DensityFunction.SimpleFunction {
        INSTANCE;

        @Override
        public double compute(FunctionContext context) {
            return 0.0;
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            Arrays.fill(output, 0.0);
        }

        @Override
        public double minValue() {
            return 0.0;
        }

        @Override
        public double maxValue() {
            return 0.0;
        }
    }

    public record Spline(CubicSpline<Point> spline) implements DensityFunction {
        private static final ThreadLocal<Point> POINT = ThreadLocal.withInitial(Point::new);

        @Override
        public double compute(FunctionContext context) {
            Point point = POINT.get();
            point.context(context);
            return spline.apply(point);
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new Spline(spline.mapAll(value -> {
                if (value instanceof Coordinate coordinate) {
                    return coordinate.mapAll(visitor);
                }
                return value;
            })));
        }

        @Override
        public double minValue() {
            return spline.minValue();
        }

        @Override
        public double maxValue() {
            return spline.maxValue();
        }

        public record Coordinate(DensityFunction function) implements CubicSpline.Value<Point> {
            @Override
            public double apply(Point point) {
                return function.compute(point.context());
            }

            @Override
            public double minValue() {
                return function.minValue();
            }

            @Override
            public double maxValue() {
                return function.maxValue();
            }

            public Coordinate mapAll(Visitor visitor) {
                return new Coordinate(function.mapAll(visitor));
            }
        }

        public static final class Point {
            private FunctionContext context;

            public FunctionContext context() {
                return context;
            }

            public void context(FunctionContext context) {
                this.context = context;
            }
        }
    }

    public record SplinePoint(double location, Object value, double derivative) {
    }

    public record Mapped(Type type, DensityFunction input, double minValue, double maxValue) implements PureTransformer {
        public static Mapped create(Type type, DensityFunction input) {
            double min = input.minValue();
            double max = input.maxValue();
            double transformedMin = transform(type, min);
            double transformedMax = transform(type, max);

            if (type == Type.INVERT) {
                if (min < 0.0 && max > 0.0) {
                    return new Mapped(type, input, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
                }
                return new Mapped(type, input, Math.min(transformedMin, transformedMax), Math.max(transformedMin, transformedMax));
            }

            if (type == Type.ABS || type == Type.SQUARE) {
                return new Mapped(type, input, Math.max(0.0, Math.min(transformedMin, transformedMax)), Math.max(transformedMin, transformedMax));
            }

            return new Mapped(type, input, Math.min(transformedMin, transformedMax), Math.max(transformedMin, transformedMax));
        }

        private static double transform(Type type, double input) {
            return type.apply(input);
        }

        @Override
        public double transform(double inputValue) {
            return transform(type, inputValue);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(create(type, input.mapAll(visitor)));
        }

        public enum Type {
            ABS {
                @Override
                double apply(double input) {
                    return Math.abs(input);
                }
            },
            SQUARE {
                @Override
                double apply(double input) {
                    return input * input;
                }
            },
            CUBE {
                @Override
                double apply(double input) {
                    return input * input * input;
                }
            },
            HALF_NEGATIVE {
                @Override
                double apply(double input) {
                    return input > 0.0 ? input : input * 0.5;
                }
            },
            QUARTER_NEGATIVE {
                @Override
                double apply(double input) {
                    return input > 0.0 ? input : input * 0.25;
                }
            },
            INVERT {
                @Override
                double apply(double input) {
                    return input == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / input;
                }
            },
            SQUEEZE {
                @Override
                double apply(double input) {
                    double clamped = NukkitMath.clamp(input, -1.0, 1.0);
                    return clamped / 2.0 - clamped * clamped * clamped / 24.0;
                }
            };

            abstract double apply(double input);
        }
    }

    public static final class Marker implements DensityFunction {
        private final Type type;
        private final DensityFunction wrapped;
        private final ThreadLocal<CacheState> cache;

        private Marker(Type type, DensityFunction wrapped) {
            this.type = type;
            this.wrapped = wrapped;
            this.cache = ThreadLocal.withInitial(() -> switch (type) {
                case FLAT_CACHE, CACHE_2D -> new Cache2DState();
                case CACHE_ONCE, CACHE_ALL_IN_CELL -> new Cache3DState();
                case INTERPOLATED -> new InterpolatedState();
            });
        }

        public static DensityFunction create(Type type, DensityFunction wrapped) {
            return switch (type) {
                default -> new Marker(type, wrapped);
            };
        }

        @Override
        public double compute(FunctionContext context) {
            return switch (type) {
                case FLAT_CACHE, CACHE_2D -> compute2d(context);
                case CACHE_ONCE, CACHE_ALL_IN_CELL -> compute3d(context);
                case INTERPOLATED -> computeInterpolated(context);
            };
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            for (int i = 0; i < output.length; i++) {
                output[i] = compute(contextProvider.forIndex(i));
            }
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(create(type, wrapped.mapAll(visitor)));
        }

        @Override
        public double minValue() {
            return wrapped.minValue();
        }

        @Override
        public double maxValue() {
            return wrapped.maxValue();
        }

        private double compute2d(FunctionContext context) {
            Cache2DState state = (Cache2DState) cache.get();
            if (state.valid && state.blockX == context.blockX() && state.blockZ == context.blockZ()) {
                return state.value;
            }

            double value = wrapped.compute(context);
            state.blockX = context.blockX();
            state.blockZ = context.blockZ();
            state.value = value;
            state.valid = true;
            return value;
        }

        private double compute3d(FunctionContext context) {
            Cache3DState state = (Cache3DState) cache.get();
            if (state.valid
                    && state.blockX == context.blockX()
                    && state.blockY == context.blockY()
                    && state.blockZ == context.blockZ()) {
                return state.value;
            }

            double value = wrapped.compute(context);
            state.blockX = context.blockX();
            state.blockY = context.blockY();
            state.blockZ = context.blockZ();
            state.value = value;
            state.valid = true;
            return value;
        }

        private double computeInterpolated(FunctionContext context) {
            InterpolatedState state = (InterpolatedState) cache.get();
            int cellX = Math.floorDiv(context.blockX(), 4) * 4;
            int cellY = Math.floorDiv(context.blockY(), 8) * 8;
            int cellZ = Math.floorDiv(context.blockZ(), 4) * 4;
            InterpolatedCell cell = state.getOrCreateCell(cellX, cellY, cellZ, wrapped);

            double xAlpha = Math.floorMod(context.blockX(), 4) / 4.0;
            double yAlpha = Math.floorMod(context.blockY(), 8) / 8.0;
            double zAlpha = Math.floorMod(context.blockZ(), 4) / 4.0;

            double x00 = lerp(cell.d000, cell.d100, xAlpha);
            double x10 = lerp(cell.d010, cell.d110, xAlpha);
            double x01 = lerp(cell.d001, cell.d101, xAlpha);
            double x11 = lerp(cell.d011, cell.d111, xAlpha);
            double z0 = lerp(x00, x10, zAlpha);
            double z1 = lerp(x01, x11, zAlpha);
            return lerp(z0, z1, yAlpha);
        }

        private static double lerp(double first, double second, double alpha) {
            return first + alpha * (second - first);
        }

        private sealed interface CacheState permits Cache2DState, Cache3DState, InterpolatedState {
        }

        private static final class Cache2DState implements CacheState {
            private boolean valid;
            private int blockX;
            private int blockZ;
            private double value;
        }

        private static final class Cache3DState implements CacheState {
            private boolean valid;
            private int blockX;
            private int blockY;
            private int blockZ;
            private double value;
        }

        private static final class InterpolatedState implements CacheState {
            private final MutableFunctionContext context = new MutableFunctionContext();
            private final Map<Long, InterpolatedCell> cells = new LinkedHashMap<>(256, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, InterpolatedCell> eldest) {
                    return size() > 512;
                }
            };

            private InterpolatedCell getOrCreateCell(int cellX, int cellY, int cellZ, DensityFunction wrapped) {
                long key = pack(cellX, cellY, cellZ);
                InterpolatedCell cell = cells.get(key);
                if (cell != null) {
                    return cell;
                }

                int nextX = cellX + 4;
                int nextY = cellY + 8;
                int nextZ = cellZ + 4;
                cell = new InterpolatedCell(
                        wrapped.compute(context.set(cellX, cellY, cellZ)),
                        wrapped.compute(context.set(nextX, cellY, cellZ)),
                        wrapped.compute(context.set(cellX, cellY, nextZ)),
                        wrapped.compute(context.set(nextX, cellY, nextZ)),
                        wrapped.compute(context.set(cellX, nextY, cellZ)),
                        wrapped.compute(context.set(nextX, nextY, cellZ)),
                        wrapped.compute(context.set(cellX, nextY, nextZ)),
                        wrapped.compute(context.set(nextX, nextY, nextZ))
                );
                cells.put(key, cell);
                return cell;
            }

            private static long pack(int cellX, int cellY, int cellZ) {
                long x = ((long) cellX & 0x1FFFFFL) << 42;
                long y = ((long) cellY & 0x1FFFFFL) << 21;
                long z = (long) cellZ & 0x1FFFFFL;
                return x | y | z;
            }
        }

        private record InterpolatedCell(
                double d000,
                double d100,
                double d010,
                double d110,
                double d001,
                double d101,
                double d011,
                double d111
        ) {
        }

        public enum Type {
            INTERPOLATED,
            FLAT_CACHE,
            CACHE_2D,
            CACHE_ONCE,
            CACHE_ALL_IN_CELL
        }
    }

    interface ShiftNoise extends DensityFunction {
        NoiseHolder offsetNoise();

        default double computeShift(double x, double y, double z) {
            return offsetNoise().getValue(x * 0.25, y * 0.25, z * 0.25) * 4.0;
        }

        @Override
        default void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }

        @Override
        default double minValue() {
            return -maxValue();
        }

        @Override
        default double maxValue() {
            return offsetNoise().maxValue() * 4.0;
        }
    }

    public record Shift(NoiseHolder offsetNoise) implements ShiftNoise {
        @Override
        public double compute(FunctionContext context) {
            return computeShift(context.blockX(), context.blockY(), context.blockZ());
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new Shift(visitor.visitNoise(offsetNoise)));
        }
    }

    public record ShiftA(NoiseHolder offsetNoise) implements ShiftNoise {
        @Override
        public double compute(FunctionContext context) {
            return computeShift(context.blockX(), 0.0, context.blockZ());
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new ShiftA(visitor.visitNoise(offsetNoise)));
        }
    }

    public record ShiftB(NoiseHolder offsetNoise) implements ShiftNoise {
        @Override
        public double compute(FunctionContext context) {
            return computeShift(context.blockZ(), context.blockX(), 0.0);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new ShiftB(visitor.visitNoise(offsetNoise)));
        }
    }

    public record ShiftedNoise(
            DensityFunction shiftX,
            DensityFunction shiftY,
            DensityFunction shiftZ,
            double xzScale,
            double yScale,
            NoiseHolder noise
    ) implements DensityFunction {
        @Override
        public double compute(FunctionContext context) {
            double x = context.blockX() * xzScale + shiftX.compute(context);
            double y = context.blockY() * yScale + shiftY.compute(context);
            double z = context.blockZ() * xzScale + shiftZ.compute(context);
            return noise.getValue(x, y, z);
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            contextProvider.fillAllDirectly(output, this);
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new ShiftedNoise(
                    shiftX.mapAll(visitor),
                    shiftY.mapAll(visitor),
                    shiftZ.mapAll(visitor),
                    xzScale,
                    yScale,
                    visitor.visitNoise(noise)
            ));
        }

        @Override
        public double minValue() {
            return -maxValue();
        }

        @Override
        public double maxValue() {
            return noise.maxValue();
        }
    }

    interface TwoArgumentSimpleFunction extends DensityFunction {
        static DensityFunction create(Type type, DensityFunction argument1, DensityFunction argument2) {
            double min1 = argument1.minValue();
            double min2 = argument2.minValue();
            double max1 = argument1.maxValue();
            double max2 = argument2.maxValue();

            double minValue = switch (type) {
                case ADD -> min1 + min2;
                case MUL -> min1 > 0.0 && min2 > 0.0
                        ? min1 * min2
                        : (max1 < 0.0 && max2 < 0.0 ? max1 * max2 : Math.min(min1 * max2, max1 * min2));
                case MIN -> Math.min(min1, min2);
                case MAX -> Math.max(min1, min2);
            };

            double maxValue = switch (type) {
                case ADD -> max1 + max2;
                case MUL -> min1 > 0.0 && min2 > 0.0
                        ? max1 * max2
                        : (max1 < 0.0 && max2 < 0.0 ? min1 * min2 : Math.max(min1 * min2, max1 * max2));
                case MIN -> Math.min(max1, max2);
                case MAX -> Math.max(max1, max2);
            };

            if ((type == Type.ADD || type == Type.MUL) && argument1 instanceof Constant constant) {
                return new MulOrAdd(type == Type.ADD ? MulOrAdd.Type.ADD : MulOrAdd.Type.MUL, argument2, minValue, maxValue, constant.value);
            }

            if ((type == Type.ADD || type == Type.MUL) && argument2 instanceof Constant constant) {
                return new MulOrAdd(type == Type.ADD ? MulOrAdd.Type.ADD : MulOrAdd.Type.MUL, argument1, minValue, maxValue, constant.value);
            }

            return new Ap2(type, argument1, argument2, minValue, maxValue);
        }

        enum Type {
            ADD,
            MUL,
            MIN,
            MAX
        }
    }

    record Ap2(Type type, DensityFunction argument1, DensityFunction argument2, double minValue, double maxValue)
            implements TwoArgumentSimpleFunction {
        @Override
        public double compute(FunctionContext context) {
            double v1 = argument1.compute(context);
            return switch (type) {
                case ADD -> v1 + argument2.compute(context);
                case MUL -> v1 == 0.0 ? 0.0 : v1 * argument2.compute(context);
                case MIN -> v1 < argument2.minValue() ? v1 : Math.min(v1, argument2.compute(context));
                case MAX -> v1 > argument2.maxValue() ? v1 : Math.max(v1, argument2.compute(context));
            };
        }

        @Override
        public void fillArray(double[] output, ContextProvider contextProvider) {
            argument1.fillArray(output, contextProvider);
            switch (type) {
                case ADD -> {
                    double[] other = new double[output.length];
                    argument2.fillArray(other, contextProvider);
                    for (int i = 0; i < output.length; i++) {
                        output[i] += other[i];
                    }
                }
                case MUL -> {
                    for (int i = 0; i < output.length; i++) {
                        double value = output[i];
                        output[i] = value == 0.0 ? 0.0 : value * argument2.compute(contextProvider.forIndex(i));
                    }
                }
                case MIN -> {
                    double min = argument2.minValue();
                    for (int i = 0; i < output.length; i++) {
                        double value = output[i];
                        output[i] = value < min ? value : Math.min(value, argument2.compute(contextProvider.forIndex(i)));
                    }
                }
                case MAX -> {
                    double max = argument2.maxValue();
                    for (int i = 0; i < output.length; i++) {
                        double value = output[i];
                        output[i] = value > max ? value : Math.max(value, argument2.compute(contextProvider.forIndex(i)));
                    }
                }
            }
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(TwoArgumentSimpleFunction.create(type, argument1.mapAll(visitor), argument2.mapAll(visitor)));
        }
    }

    record MulOrAdd(Type specificType, DensityFunction input, double minValue, double maxValue, double argument)
            implements TwoArgumentSimpleFunction, PureTransformer {
        @Override
        public double transform(double inputValue) {
            return switch (specificType) {
                case MUL -> inputValue * argument;
                case ADD -> inputValue + argument;
            };
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            DensityFunction mapped = input.mapAll(visitor);
            double min = mapped.minValue();
            double max = mapped.maxValue();
            double newMin;
            double newMax;

            if (specificType == Type.ADD) {
                newMin = min + argument;
                newMax = max + argument;
            } else if (argument >= 0.0) {
                newMin = min * argument;
                newMax = max * argument;
            } else {
                newMin = max * argument;
                newMax = min * argument;
            }

            return new MulOrAdd(specificType, mapped, newMin, newMax, argument);
        }

        enum Type {
            MUL,
            ADD
        }
    }

    interface PureTransformer extends DensityFunction {
        DensityFunction input();

        double transform(double inputValue);

        @Override
        default double compute(FunctionContext context) {
            return transform(input().compute(context));
        }

        @Override
        default void fillArray(double[] output, ContextProvider contextProvider) {
            input().fillArray(output, contextProvider);
            for (int i = 0; i < output.length; i++) {
                output[i] = transform(output[i]);
            }
        }
    }

    interface TransformerWithContext extends DensityFunction {
        DensityFunction input();

        double transform(FunctionContext context, double inputValue);

        @Override
        default double compute(FunctionContext context) {
            return transform(context, input().compute(context));
        }

        @Override
        default void fillArray(double[] output, ContextProvider contextProvider) {
            for (int i = 0; i < output.length; i++) {
                FunctionContext context = contextProvider.forIndex(i);
                output[i] = transform(context, input().compute(context));
            }
        }
    }

    private static double clampedMap(double value, double fromY, double toY, double fromValue, double toValue) {
        if (fromY == toY) {
            return value < fromY ? fromValue : toValue;
        }
        double t = NukkitMath.clamp((value - fromY) / (toY - fromY), 0.0, 1.0);
        return fromValue + t * (toValue - fromValue);
    }
}
