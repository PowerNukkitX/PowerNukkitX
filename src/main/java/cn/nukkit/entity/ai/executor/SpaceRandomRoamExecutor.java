package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitXOnly
@Since("1.19.50-r3")
public class SpaceRandomRoamExecutor implements EntityControl, IBehaviorExecutor {
    protected float speed;
    protected int maxRoamRange;
    protected int frequency;
    protected int currentTargetCalTick = 0;
    protected int durationTick = 0;
    protected boolean calNextTargetImmediately = false;
    protected int runningTime;
    protected int maxRetryTime;

    public SpaceRandomRoamExecutor(float speed, int maxRoamRange, int frequency) {
        this(speed, maxRoamRange, frequency, false, 100);
    }


    public SpaceRandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, 10);
    }

    public SpaceRandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, int maxRetryTime) {
        this.speed = speed;
        this.maxRoamRange = maxRoamRange;
        this.frequency = frequency;
        this.currentTargetCalTick = this.frequency;
        this.calNextTargetImmediately = calNextTargetImmediately;
        this.runningTime = runningTime;
        this.maxRetryTime = maxRetryTime;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        currentTargetCalTick++;
        durationTick++;
        if (entity.isEnablePitch()) entity.setEnablePitch(false);
        if (entity.isInsideOfWater()) {
            if (currentTargetCalTick >= frequency || (calNextTargetImmediately && needUpdateTarget(entity))) {
                Vector3 target = next(entity);
                if (entity.getMovementSpeed() != speed)
                    entity.setMovementSpeed(speed);
                //更新寻路target
                setRouteTarget(entity, target);
                //更新视线target
                setLookTarget(entity, target);
                currentTargetCalTick = 0;
                entity.getBehaviorGroup().setForceUpdateRoute(calNextTargetImmediately);
            }
        }
        if (durationTick <= runningTime || runningTime == -1)
            return true;
        else {
            currentTargetCalTick = 0;
            durationTick = 0;
            return false;
        }
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
    }

    protected void stop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setEnablePitch(true);
        currentTargetCalTick = 0;
        durationTick = 0;
    }

    private boolean needUpdateTarget(EntityIntelligent entity) {
        return entity.getMoveTarget() == null;
    }

    protected Vector3 next(EntityIntelligent entity) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int z = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        int y = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorY();
        return new Vector3(x, y, z);
    }
}
