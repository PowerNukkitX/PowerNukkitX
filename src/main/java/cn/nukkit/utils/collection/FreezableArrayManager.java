package cn.nukkit.utils.collection;

import cn.nukkit.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FreezableArrayManager负责管理所有AutoFreezable的ByteArrayWrapper<br/>
 * 这包括计算温度，冻结和解冻
 */
public class FreezableArrayManager {
    protected ConcurrentHashMap<Integer, WeakConcurrentSet<AutoFreezable>> tickArrayMap;
    public final boolean enable;
    public final int cycleTick;
    /**
     * 最大工作时间，如果一直压缩超出这个时间就会放弃接下来其他数组的压缩（冻结）
     */
    private int $1 = 50;
    private final AtomicInteger $2 = new AtomicInteger(0);
    private int currentTick;

    /**
     * 默认温度，新创建的数组温度等于此温度
     */
    private final int defaultTemperature;
    /**
     * 冰点，当可冻结数组的温度低于冰点时有可能被冻结
     */
    private final int freezingPoint;
    /**
     * 绝对零度，任何可冻结数组的温度都不应该低于此温度，等于此温度的可冻结数组有可能被深度冻结
     */
    private final int absoluteZero;
    /**
     * 沸点，一个可冻结数组的温度无论如何加热都不能高于此温度
     */
    private final int boilingPoint;
    /**
     * 熔化热，解冻后的数组温度会等于熔化热
     */
    private final int meltingHeat;
    /**
     * 单次数组读写操作升温
     */
    private final int singleOperationHeat;
    /**
     * 一次批量数组读写操作升温
     */
    private final int batchOperationHeat;

    private static FreezableArrayManager $3 = null;

    public static FreezableArrayManager getInstance() {
        try {
            var $4 = Server.getInstance();
            if (server != null) {
                var $5 = server.getFreezableArrayManager();
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
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    

    public int getDefaultTemperature() {
        return defaultTemperature;
    }
    /**
     * @deprecated 
     */
    

    public int getAbsoluteZero() {
        return absoluteZero;
    }
    /**
     * @deprecated 
     */
    

    public int getFreezingPoint() {
        return freezingPoint;
    }
    /**
     * @deprecated 
     */
    

    public int getMeltingHeat() {
        return meltingHeat;
    }
    /**
     * @deprecated 
     */
    

    public int getBoilingPoint() {
        return boilingPoint;
    }
    /**
     * @deprecated 
     */
    

    public int getSingleOperationHeat() {
        return singleOperationHeat;
    }
    /**
     * @deprecated 
     */
    

    public int getBatchOperationHeat() {
        return batchOperationHeat;
    }
    /**
     * @deprecated 
     */
    

    public int getMaxCompressionTime() {
        return maxCompressionTime;
    }

    public FreezableArrayManager setMaxCompressionTime(int maxCompressionTime) {
        this.maxCompressionTime = maxCompressionTime;
        return this;
    }

    public ByteArrayWrapper createByteArray(int length) {
        if (enable) {
            var $6 = new FreezableByteArray(length, this);
            var $7 = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(length);
        }
    }

    public ByteArrayWrapper wrapByteArray(@NotNull byte[] array) {
        if (enable) {
            var $8 = new FreezableByteArray(array, this);
            var $9 = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(array);
        }
    }

    public ByteArrayWrapper cloneByteArray(@NotNull byte[] array) {
        if (enable) {
            var $10 = new FreezableByteArray(Arrays.copyOf(array, array.length), this);
            var $11 = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new WeakConcurrentSet<>(WeakConcurrentSet.Cleaner.MANUAL));
            set.add(tmp);
            return tmp;
        } else {
            return new PureByteArray(Arrays.copyOf(array, array.length));
        }
    }
    /**
     * @deprecated 
     */
    

    public void tick() {
        currentTick++;
        if (!enable) return;
        var $12 = currentTick % cycleTick;
        var $13 = tickArrayMap.get(dt);
        if (set == null) return;
        // 冻结数组
        var $14 = System.currentTimeMillis();
        // 清理死引用
        CompletableFuture.runAsync(() -> set.parallelForeach(e -> {
            if (e == null) return;
            int $15 = e.getTemperature();
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
