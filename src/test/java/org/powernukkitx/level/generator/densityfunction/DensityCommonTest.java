package org.powernukkitx.level.generator.densityfunction;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DensityCommonTest {
    @Test
    void cache2dCachesByHorizontalPosition() {
        CountingFunction source = new CountingFunction(context -> context.blockY() + 1.0);
        DensityFunction cached = DensityCommon.cache2d(source);
        DensityCommon.CellFunctionContext context = context(new DensityCommon.ChunkCache(), 0, 0, 0);

        assertEquals(1.0, cached.compute(context));
        assertEquals(1.0, cached.compute(context.set(0, 10, 0)));
        assertEquals(1, source.computations());

        assertEquals(6.0, cached.compute(context.set(1, 5, 0)));
        assertEquals(2, source.computations());
    }

    @Test
    void cacheOnceCachesExactPositionIncludingOrigin() {
        CountingFunction source = new CountingFunction(DensityCommonTest::coordinateValue);
        DensityFunction cached = DensityCommon.cacheOnce(source);
        DensityCommon.CellFunctionContext context = context(new DensityCommon.ChunkCache(), 0, 0, 0);

        assertEquals(0.0, cached.compute(context));
        assertEquals(0.0, cached.compute(context));
        assertEquals(1, source.computations());

        assertEquals(100.0, cached.compute(context.set(0, 1, 0)));
        assertEquals(2, source.computations());
    }

    @Test
    void cachesAreIsolatedByChunkCache() {
        CountingFunction source = new CountingFunction(context -> sourceValue(context.blockY()));
        DensityFunction cached = DensityCommon.cache2d(source);

        assertEquals(2.0, cached.compute(context(new DensityCommon.ChunkCache(), 3, 1, 4)));
        assertEquals(8.0, cached.compute(context(new DensityCommon.ChunkCache(), 3, 7, 4))); 
        assertEquals(2, source.computations());
    }

    @Test
    void clearingChunkCacheDiscardsMarkerState() {
        DensityCommon.ChunkCache chunkCache = new DensityCommon.ChunkCache();
        CountingFunction source = new CountingFunction(DensityCommonTest::coordinateValue);
        DensityFunction cached = DensityCommon.cacheOnce(source);
        DensityCommon.CellFunctionContext context = context(chunkCache, 1, 2, 3);

        assertEquals(231.0, cached.compute(context));
        assertEquals(231.0, cached.compute(context));
        chunkCache.clear();
        assertEquals(231.0, cached.compute(context));
        assertEquals(2, source.computations());
    }

    @Test
    void flatCacheUsesYZeroAndPropagatesChunkCache() {
        CountingFunction source = new CountingFunction(DensityCommonTest::coordinateValue);
        DensityFunction cached = DensityCommon.flatCache(DensityCommon.cacheOnce(source));
        DensityCommon.CellFunctionContext context = context(new DensityCommon.ChunkCache(), -1, 20, -1);

        assertEquals(-11.0, cached.compute(context));
        assertEquals(-11.0, cached.compute(context.set(-1, 40, -1)));
        assertEquals(1, source.computations());
    }

    @Test
    void cacheAllInCellPrecomputesEachPositionOnce() {
        CountingFunction source = new CountingFunction(DensityCommonTest::coordinateValue);
        DensityFunction cached = DensityCommon.cacheAllInCell(source);
        DensityCommon.CellFunctionContext context = context(new DensityCommon.ChunkCache(), 0, 0, 0);

        assertEquals(0.0, cached.compute(context));
        assertEquals(123.0, cached.compute(context.set(3, 1, 2)));
        assertEquals(128, source.computations());

        assertEquals(400.0, cached.compute(context.set(0, 4, 0)));
        assertEquals(128, source.computations());
    }

    @Test
    void interpolatedPreservesLinearFunctionAndReusesCorners() {
        CountingFunction source = new CountingFunction(DensityCommonTest::coordinateValue);
        DensityFunction interpolated = DensityCommon.interpolated(source);
        DensityCommon.CellFunctionContext context = context(new DensityCommon.ChunkCache(), 1, 2, 3);

        assertEquals(231.0, interpolated.compute(context));
        assertEquals(222.0, interpolated.compute(context.set(2, 2, 2)));
        assertEquals(8, source.computations());

        assertEquals(234.0, interpolated.compute(context.set(4, 2, 3)));
        assertEquals(12, source.computations());
    }

    @Test
    void cacheMarkersRequireChunkCacheContext() {
        DensityFunction cached = DensityCommon.cacheOnce(DensityCommon.constant(1.0));
        DensityFunction.FunctionContext context = new DensityFunction.SinglePointContext(0, 0, 0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> cached.compute(context));
        assertEquals("Density function requires ChunkCacheContext, got: " + context.getClass().getName(), exception.getMessage());
    }

    private static DensityCommon.CellFunctionContext context(
            DensityCommon.ChunkCache chunkCache, int blockX, int blockY, int blockZ
    ) {
        return new DensityCommon.CellFunctionContext(chunkCache).set(blockX, blockY, blockZ);
    }

    private static double coordinateValue(DensityFunction.FunctionContext context) {
        return context.blockX() + context.blockY() * 100.0 + context.blockZ() * 10.0;
    }

    private static double sourceValue(int value) {
        return value + 1.0;
    }

    private static final class CountingFunction implements DensityFunction.SimpleFunction {
        private final ToDoubleFunction<DensityFunction.FunctionContext> function;
        private final AtomicInteger computations = new AtomicInteger();

        private CountingFunction(ToDoubleFunction<DensityFunction.FunctionContext> function) {
            this.function = function;
        }

        @Override
        public double compute(FunctionContext context) {
            computations.incrementAndGet();
            return function.applyAsDouble(context);
        }

        @Override
        public double minValue() {
            return Double.NEGATIVE_INFINITY;
        }

        @Override
        public double maxValue() {
            return Double.POSITIVE_INFINITY;
        }

        private int computations() {
            return computations.get();
        }
    }
}
