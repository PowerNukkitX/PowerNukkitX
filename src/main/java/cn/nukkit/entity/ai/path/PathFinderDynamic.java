package cn.nukkit.entity.ai.path;

import cn.nukkit.math.NukkitMath;

/**
 * 实现了此接口的寻路器可以动态寻路
 */
public interface PathFinderDynamic extends PathFinder {
    void allowDestinationOffset(int doubleOffset);

    default void allowDestinationOffset(float offset) {
        allowDestinationOffset(NukkitMath.floorFloat(offset * 2));
    }

    long getAllowedOffsetSquared();
}
