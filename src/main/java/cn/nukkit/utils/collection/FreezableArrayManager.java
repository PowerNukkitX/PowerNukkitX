package cn.nukkit.utils.collection;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * EconomicArrayManager负责管理所有AutoFreezable的ByteArrayWrapper<br/>
 * 这包括计算温度，冻结和解冻
 */
@PowerNukkitXOnly
@Since("1.19.40-r4")
public class FreezableArrayManager {
    protected ConcurrentHashMap<Integer, CopyOnWriteArrayList<Object>> tickArrayMap;
    public final int cycleTick;
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
     * 熔化热，解冻后的数组温度会等于熔化热
     */
    private final int meltingHeat;

    public FreezableArrayManager(int cycleTick, int freezingPoint, int absoluteZero, int meltingHeat) {
        this.cycleTick = cycleTick;
        this.freezingPoint = freezingPoint;
        this.absoluteZero = absoluteZero;
        this.tickArrayMap = new ConcurrentHashMap<>(cycleTick + 1, 0.999f);
        this.meltingHeat = meltingHeat;
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
}
