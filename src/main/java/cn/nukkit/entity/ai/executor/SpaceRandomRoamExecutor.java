package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 3D随机漫游
 */
@PowerNukkitXOnly
@Since("1.19.50-r4")
public class SpaceRandomRoamExecutor extends FlatRandomRoamExecutor {

    protected int maxYRoamRange;

    public SpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency) {
        this(speed, maxXZRoamRange, maxYRoamRange, frequency, false, 100);
    }

    public SpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxXZRoamRange, maxYRoamRange, frequency, calNextTargetImmediately, runningTime,true, 10);
    }

    public SpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime) {
        super(speed, maxXZRoamRange, frequency, calNextTargetImmediately, runningTime, avoidWater, maxRetryTime);
        this.maxYRoamRange = maxYRoamRange;
    }

    @Override
    protected Vector3 next(EntityIntelligent entity) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int z = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        int y = random.nextInt(maxYRoamRange * 2) - maxYRoamRange + entity.getFloorY();
        return new Vector3(x, y, z);
    }
}
