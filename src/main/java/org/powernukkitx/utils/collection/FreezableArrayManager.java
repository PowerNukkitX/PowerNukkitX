package org.powernukkitx.utils.collection;

import org.powernukkitx.Server;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FreezableArrayManager is responsible for managing all AutoFreezable ByteArrayWrappers.<br/>
 * This includes computing temperatures, freezing and thawing.
 */
public class FreezableArrayManager {
    protected ConcurrentHashMap<Integer, FreezableBucket> tickArrayMap;
    public final boolean enable;
    public final int cycleTick;
    /**
     * Maximum working time; if compression keeps running past this time, the compression (freezing) of the remaining arrays is abandoned.
     */
    private int maxCompressionTime = 50;
    /**
     * Hard cap on how many arrays a single cycle may compress, on top of {@link #maxCompressionTime}.
     * The time budget alone lets one cycle occupy a compute thread until it expires; capping the
     * count keeps the pool available for chunk work, and anything skipped is simply retried on the
     * next pass over the same bucket.
     */
    private int maxCompressionsPerCycle = 2048;
    /** Maximum number of entries inspected by one asynchronous cycle. */
    private int maxScansPerCycle = 16384;
    private final AtomicInteger currentArrayId = new AtomicInteger(0);
    /**
     * Guards against stacking up cycle tasks on the compute thread pool when a cycle takes longer than
     * {@link #cycleTick} ticks to finish.
     */
    private final AtomicBoolean cycleRunning = new AtomicBoolean(false);
    private volatile long currentTick;
    /** Number of completed cooling sweeps used by freezable arrays for lazy temperature decay. */
    private volatile int coolingCycle;

    /**
     * Default temperature; a newly created array's temperature equals this value.
     */
    private final int defaultTemperature;
    /**
     * Freezing point; when a freezable array's temperature drops below the freezing point, it may be frozen.
     */
    private final int freezingPoint;
    /**
     * Absolute zero; no freezable array's temperature should ever drop below this value, and a freezable array at exactly this temperature may be deep-frozen.
     */
    private final int absoluteZero;
    /**
     * Boiling point; a freezable array's temperature can never rise above this value no matter how much it is heated.
     */
    private final int boilingPoint;
    /**
     * Heat of fusion; a thawed array's temperature is set to this value.
     */
    private final int meltingHeat;
    /**
     * Temperature rise for a single array read/write operation.
     */
    private final int singleOperationHeat;
    /**
     * Temperature rise for a batch array read/write operation.
     */
    private final int batchOperationHeat;

    private static FreezableArrayManager fallbackInstance = null;

    public static FreezableArrayManager getInstance() {
        try {
            var server = Server.getInstance();
            if (server != null) {
                var tmp = server.getFreezableArrayManager();
                if (tmp != null) {
                    return tmp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fallbackInstance == null) {
            fallbackInstance = new FreezableArrayManager(true, 32, 32, 0, -256, 1024, 16, 1, 32);
            System.err.println("Cannot get FreezableArrayManager from Server instance, using a fallback instance!");
        }
        return fallbackInstance;

    }

    public FreezableArrayManager(boolean enable, int cycleTick, int defaultTemperature, int freezingPoint, int absoluteZero, int boilingPoint, int meltingHeat, int singleOperationHeat, int batchOperationHeat) {
        this.enable = enable;
        this.cycleTick = cycleTick;
        this.defaultTemperature = defaultTemperature;
        this.freezingPoint = freezingPoint;
        this.absoluteZero = absoluteZero;
        this.tickArrayMap = new ConcurrentHashMap<>(cycleTick + 1, 0.999f);
        this.boilingPoint = boilingPoint;
        this.meltingHeat = meltingHeat;
        this.singleOperationHeat = singleOperationHeat;
        this.batchOperationHeat = batchOperationHeat;
    }

    public int getDefaultTemperature() {
        return defaultTemperature;
    }

    public int getAbsoluteZero() {
        return absoluteZero;
    }

    public int getFreezingPoint() {
        return freezingPoint;
    }

    public int getMeltingHeat() {
        return meltingHeat;
    }

    public int getBoilingPoint() {
        return boilingPoint;
    }

    public int getSingleOperationHeat() {
        return singleOperationHeat;
    }

    public int getBatchOperationHeat() {
        return batchOperationHeat;
    }

    public int getMaxCompressionTime() {
        return maxCompressionTime;
    }

    public FreezableArrayManager setMaxCompressionTime(int maxCompressionTime) {
        this.maxCompressionTime = maxCompressionTime;
        return this;
    }

    public int getMaxCompressionsPerCycle() {
        return maxCompressionsPerCycle;
    }

    public FreezableArrayManager setMaxCompressionsPerCycle(int maxCompressionsPerCycle) {
        this.maxCompressionsPerCycle = maxCompressionsPerCycle;
        return this;
    }

    /**
     * @return the maximum number of tracked arrays inspected during one asynchronous cycle
     */
    public int getMaxScansPerCycle() {
        return maxScansPerCycle;
    }

    /**
     * Sets the scan limit used by future cycles.
     *
     * @param maxScansPerCycle maximum number of entries to inspect
     * @return this manager
     */
    public FreezableArrayManager setMaxScansPerCycle(int maxScansPerCycle) {
        this.maxScansPerCycle = maxScansPerCycle;
        return this;
    }

    /**
     * @return the number of completed cooling cycles
     */
    public int getCoolingCycle() {
        return coolingCycle;
    }

    private boolean isFreezeCandidate(AutoFreezable e) {
        return switch (e.getFreezeStatus()) {
            case NONE -> e.getTemperature() <= freezingPoint;
            case FREEZE -> e.getTemperature() <= absoluteZero;
            default -> false;
        };
    }

    public ByteArrayWrapper createByteArray(int length) {
        if (enable) {
            var tmp = new FreezableByteArray(length, this);
            var bucket = tickArrayMap.computeIfAbsent(Math.floorMod(currentArrayId.getAndIncrement(), cycleTick), (ignore) -> new FreezableBucket());
            bucket.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(length);
        }
    }

    public ByteArrayWrapper wrapByteArray(@NotNull byte[] array) {
        if (enable) {
            var tmp = new FreezableByteArray(array, this);
            var bucket = tickArrayMap.computeIfAbsent(Math.floorMod(currentArrayId.getAndIncrement(), cycleTick), (ignore) -> new FreezableBucket());
            bucket.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(array);
        }
    }

    public ByteArrayWrapper cloneByteArray(@NotNull byte[] array) {
        if (enable) {
            var tmp = new FreezableByteArray(Arrays.copyOf(array, array.length), this);
            var bucket = tickArrayMap.computeIfAbsent(Math.floorMod(currentArrayId.getAndIncrement(), cycleTick), (ignore) -> new FreezableBucket());
            bucket.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(Arrays.copyOf(array, array.length));
        }
    }

    public void tick() {
        currentTick++;
        if (!enable) return;
        var dt = (int) Math.floorMod(currentTick, (long) cycleTick);
        if (dt == 0) coolingCycle++;
        var bucket = tickArrayMap.get(dt);
        if (bucket == null) return;
        if (!cycleRunning.compareAndSet(false, true)) return;

        final int scanBudget = maxScansPerCycle;
        final int compressionBudget = maxCompressionsPerCycle;
        final int timeBudget = maxCompressionTime;
        CompletableFuture.runAsync(() -> {
            final long deadline = System.currentTimeMillis() + timeBudget;
            final List<AutoFreezable> candidates = new ArrayList<>();
            bucket.scan(scanBudget, this::isFreezeCandidate, candidates);

            int budget = compressionBudget;
            for (AutoFreezable e : candidates) {
                if (budget-- <= 0 || System.currentTimeMillis() > deadline) break;
                if (e.getTemperature() <= absoluteZero) {
                    e.deepFreeze();
                } else {
                    e.freeze();
                }
            }
        }, Server.getInstance().getComputeThreadPool())
                .whenComplete((ignored, throwable) -> cycleRunning.set(false));
    }
}
