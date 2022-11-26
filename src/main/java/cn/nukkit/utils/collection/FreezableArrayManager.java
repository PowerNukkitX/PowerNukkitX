package cn.nukkit.utils.collection;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EconomicArrayManager负责管理所有AutoFreezable的ByteArrayWrapper<br/>
 * 这包括计算温度，冻结和解冻
 */
@PowerNukkitXOnly
@Since("1.19.40-r4")
public class FreezableArrayManager {
    protected ConcurrentHashMap<Integer, CopyOnWriteArrayList<WeakReference<AutoFreezable>>> tickArrayMap;
    public final int cycleTick;
    /**
     * 最大工作时间，如果一直压缩超出这个时间就会放弃接下来其他数组的压缩（冻结）
     */
    private int maxCompressionTime = 50;
    private final AtomicInteger currentArrayId = new AtomicInteger(0);
    private int currentTick;

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
            fallbackInstance = new FreezableArrayManager(32, 0, -256, 1024, 16, 1, 32);
            System.err.println("Cannot get FreezableArrayManager from Server instance, using a fallback instance!");
        }
        return fallbackInstance;

    }

    public FreezableArrayManager(int cycleTick, int freezingPoint, int absoluteZero, int boilingPoint, int meltingHeat, int singleOperationHeat, int batchOperationHeat) {
        this.cycleTick = cycleTick;
        this.freezingPoint = freezingPoint;
        this.absoluteZero = absoluteZero;
        this.tickArrayMap = new ConcurrentHashMap<>(cycleTick + 1, 0.999f);
        this.boilingPoint = boilingPoint;
        this.meltingHeat = meltingHeat;
        this.singleOperationHeat = singleOperationHeat;
        this.batchOperationHeat = batchOperationHeat;
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

    public FreezableByteArray createByteArray(int length) {
        var tmp = new FreezableByteArray(length, this);
        var list = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new CopyOnWriteArrayList<>());
        list.add(new WeakReference<>(tmp));
        return tmp;
    }

    public FreezableByteArray wrapByteArray(@NotNull byte[] array) {
        var tmp = new FreezableByteArray(array, this);
        var list = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new CopyOnWriteArrayList<>());
        list.add(new WeakReference<>(tmp));
        return tmp;
    }

    public FreezableByteArray cloneByteArray(@NotNull byte[] array) {
        var tmp = new FreezableByteArray(Arrays.copyOf(array, array.length), this);
        var list = tickArrayMap.computeIfAbsent(currentArrayId.getAndIncrement() % cycleTick, (ignore) -> new CopyOnWriteArrayList<>());
        list.add(new WeakReference<>(tmp));
        return tmp;
    }

    public void tick() {
        currentTick++;
        var dt = currentTick % cycleTick;
        var list = tickArrayMap.get(dt);
        // 定期清理空引用
        if (dt == 0) {
            list.removeIf(r -> r.get() == null);
        }
        var start = System.currentTimeMillis();
        CompletableFuture.runAsync(() -> list.parallelStream().filter(r -> {
            var e = r.get();
            if (e == null) return false;
            int temp = e.getTemperature();
            e.colder(1);
            return temp <= getFreezingPoint() + 1;
        }).forEach(r -> {
            if (System.currentTimeMillis() - start > maxCompressionTime) {
                return;
            }
            var e = r.get();
            if (e == null) return;
            if (e.getFreezeStatus() == AutoFreezable.FreezeStatus.NONE || e.getFreezeStatus() == AutoFreezable.FreezeStatus.FREEZE) {
                if (e.getTemperature() == absoluteZero) {
                    e.deepFreeze();
                } else {
                    e.freeze();
                }
            }
        }), Server.getInstance().computeThreadPool);
    }
}
