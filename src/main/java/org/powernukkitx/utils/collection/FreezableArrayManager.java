package org.powernukkitx.utils.collection;

import org.powernukkitx.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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
    private final AtomicInteger currentArrayId = new AtomicInteger(0);
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

    public ByteArrayWrapper createByteArray(int length) {
        if (enable) {
            var tmp = new FreezableByteArray(length, this);
            var set = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(length);
        }
    }

    public ByteArrayWrapper wrapByteArray(@NotNull byte[] array) {
        if (enable) {
            var tmp = new FreezableByteArray(array, this);
            var set = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(array);
        }
    }

    public ByteArrayWrapper cloneByteArray(@NotNull byte[] array) {
        if (enable) {
            var tmp = new FreezableByteArray(Arrays.copyOf(array, array.length), this);
            var set = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(Arrays.copyOf(array, array.length));
        }
    }

    public void tick() {
        currentTick++;
        if (!enable) return;
        var dt = currentTick % cycleTick;
        var set = tickArrayMap.get(dt);
        if (set == null) return;
        // freeze arrays
        var start = System.currentTimeMillis();
        // clean up dead references
        CompletableFuture.runAsync(() -> set.parallelForeach(e -> {
            if (e == null) return;
            int temp = e.getTemperature();
            e.colder(1);
            if (temp <= getFreezingPoint() + 1) {
                if (System.currentTimeMillis() - start > maxCompressionTime) {
                    return;
                }
                if (e.getFreezeStatus() == AutoFreezable.FreezeStatus.NONE || e.getFreezeStatus() == AutoFreezable.FreezeStatus.FREEZE) {
                    if (e.getTemperature() == absoluteZero) {
                        e.deepFreeze();
                    } else {
                        e.freeze();
                    }
                }
            }
        }), Server.getInstance().getComputeThreadPool()).thenRun(set::clearDeadReferences);
    }
}
