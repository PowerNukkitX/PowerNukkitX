package org.powernukkitx.utils.collection;

import org.powernukkitx.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FreezableArrayManager is responsible for managing all AutoFreezable ByteArrayWrappers.<br/>
 * This includes computing temperatures, freezing and thawing.
 */
public class FreezableArrayManager {
    protected ConcurrentHashMap<Integer, WeakConcurrentSet<AutoFreezable>> tickArrayMap;
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
    private final AtomicInteger currentArrayId = new AtomicInteger(0);
    /**
     * Guards against stacking up cycle tasks on the compute thread pool when a cycle takes longer than
     * {@link #cycleTick} ticks to finish.
     */
    private final AtomicBoolean cycleRunning = new AtomicBoolean(false);
    private int currentTick;

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

    public ByteArrayWrapper createByteArray(int length) {
        if (enable) {
            var tmp = new FreezableByteArray(length, this);
            var set = tickArrayMap.computeIfAbsent(Math.floorMod(currentArrayId.getAndIncrement(), cycleTick), (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(length);
        }
    }

    public ByteArrayWrapper wrapByteArray(@NotNull byte[] array) {
        if (enable) {
            var tmp = new FreezableByteArray(array, this);
            var set = tickArrayMap.computeIfAbsent(Math.floorMod(currentArrayId.getAndIncrement(), cycleTick), (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(array);
        }
    }

    public ByteArrayWrapper cloneByteArray(@NotNull byte[] array) {
        if (enable) {
            var tmp = new FreezableByteArray(Arrays.copyOf(array, array.length), this);
            var set = tickArrayMap.computeIfAbsent(Math.floorMod(currentArrayId.getAndIncrement(), cycleTick), (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(Arrays.copyOf(array, array.length));
        }
    }

    public void tick() {
        currentTick++;
        if (!enable) return;
        var dt = Math.floorMod(currentTick, cycleTick);
        var set = tickArrayMap.get(dt);
        if (set == null) return;
        if (!cycleRunning.compareAndSet(false, true)) return;
        // freeze arrays
        //
        // The deadline is taken here, on the tick thread, and not where the cycle starts running on
        // the compute pool. The caller sets maxCompressionTime to whatever is left of the current
        // tick, so the window belongs to this tick: a cycle that cannot get a compute thread before
        // the tick is over is meant to give up rather than run into the next one.
        final long deadline = System.currentTimeMillis() + maxCompressionTime;
        final int budgetStart = maxCompressionsPerCycle;
        // clean up dead references
        CompletableFuture.runAsync(() -> {
            int budget = budgetStart;
            boolean budgetSpent = false;
            for (AutoFreezable e : set) {
                if (e == null) continue;
                int temp = e.getTemperature();
                // Cooling continues for the whole bucket even once the compression budget is gone.
                // Stopping the loop outright would starve its tail: the same arrays are visited in
                // the same order every cycle, so the ones past the cut-off would never cool down.
                e.colder(1);
                if (budgetSpent) continue;
                if (temp > getFreezingPoint() + 1) continue;
                var status = e.getFreezeStatus();
                if (status != AutoFreezable.FreezeStatus.NONE && status != AutoFreezable.FreezeStatus.FREEZE) continue;
                if (budget-- <= 0 || System.currentTimeMillis() > deadline) {
                    budgetSpent = true;
                    continue;
                }
                if (e.getTemperature() == absoluteZero) {
                    e.deepFreeze();
                } else {
                    e.freeze();
                }
            }
        }, Server.getInstance().getComputeThreadPool())
                .whenComplete((ignored, throwable) -> {
                    try {
                        set.clearDeadReferences();
                    } finally {
                        cycleRunning.set(false);
                    }
                });
    }
}
