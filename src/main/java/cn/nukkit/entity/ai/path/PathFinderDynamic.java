package cn.nukkit.entity.ai.path;

import cn.nukkit.math.NukkitMath;

/**
 * 实现了此接口的寻路器可以动态寻路
 */
public interface PathFinderDynamic extends PathFinder {
    /**
     * 警告，此方法会导致Destination被重设
     * @param doubleOffset 允许的偏移量的两倍
     */
    void allowDestinationOffset(int doubleOffset);

    default void allowDestinationOffset(float offset) {
        allowDestinationOffset(NukkitMath.floorFloat(offset * 2));
    }

    long getAllowedOffsetSquared();
}
