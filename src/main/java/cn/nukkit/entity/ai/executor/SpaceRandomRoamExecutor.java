package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 3D随机漫游
 */


public class SpaceRandomRoamExecutor extends FlatRandomRoamExecutor {

    protected int maxYRoamRange;
    /**
     * @deprecated 
     */
    

    public SpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency) {
        this(speed, maxXZRoamRange, maxYRoamRange, frequency, false, 100);
    }
    /**
     * @deprecated 
     */
    

    public SpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxXZRoamRange, maxYRoamRange, frequency, calNextTargetImmediately, runningTime, true, 10);
    }
    /**
     * @deprecated 
     */
    

    public SpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime) {
        super(speed, maxXZRoamRange, frequency, calNextTargetImmediately, runningTime, avoidWater, maxRetryTime);
        this.maxYRoamRange = maxYRoamRange;
    }

    @Override
    protected Vector3 next(EntityIntelligent entity) {
        var $1 = ThreadLocalRandom.current();
        int $2 = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int $3 = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        int $4 = random.nextInt(maxYRoamRange * 2) - maxYRoamRange + entity.getFloorY();
        return new Vector3(x, y, z);
    }
}
