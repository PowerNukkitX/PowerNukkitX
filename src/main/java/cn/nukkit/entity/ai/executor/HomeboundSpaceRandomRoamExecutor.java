package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 3D随机漫游
 */


public class HomeboundSpaceRandomRoamExecutor extends FlatRandomRoamExecutor {

    protected int maxYRoamRange;
    protected Position home;
    protected int homeRadius;

    public HomeboundSpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, Position home, int homeRadius) {
        this(speed, maxXZRoamRange, maxYRoamRange, frequency, false, 100, home, homeRadius);
    }

    public HomeboundSpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, Position home, int homeRadius) {
        this(speed, maxXZRoamRange, maxYRoamRange, frequency, calNextTargetImmediately, runningTime, true, 10, home, homeRadius);
    }

    public HomeboundSpaceRandomRoamExecutor(float speed, int maxXZRoamRange, int maxYRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime, Position home, int homeRadius) {
        super(speed, maxXZRoamRange, frequency, calNextTargetImmediately, runningTime, avoidWater, maxRetryTime);
        this.maxYRoamRange = maxYRoamRange;
        this.home = home;
        this.homeRadius = homeRadius;
    }

    @Override
    protected Vector3 next(EntityIntelligent entity) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(homeRadius * 2) - homeRadius + home.getFloorX();
        int z = random.nextInt(homeRadius * 2) - homeRadius + home.getFloorZ();
        int y = random.nextInt(maxYRoamRange * 2) - maxYRoamRange + home.getFloorY();
        if (x < home.getFloorX() - homeRadius || x > home.getFloorX() + homeRadius ||
                z < home.getFloorZ() - homeRadius || z > home.getFloorZ() + homeRadius) {
            return next(entity); // retry if out of bounds
        }
        return new Vector3(x, y, z);
    }
}
