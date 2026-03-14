package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;


public class FlatRandomRoamExecutor implements EntityControl, IBehaviorExecutor {

    protected float speed;
    protected int maxRoamRange;
    protected int frequency;

    protected int currentTargetCalTick;
    protected int durationTick = 0;
    protected boolean calNextTargetImmediately;
    protected int runningTime;
    protected boolean avoidWater;
    protected int maxRetryTime;

    public FlatRandomRoamExecutor(float speed, int maxRoamRange, int frequency) {
        this(speed, maxRoamRange, frequency, false, 100);
    }

    public FlatRandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, false, 10);
    }

    /**
     * Instantiates a new Flat random roam executor.
     *
     * @param speed                    Movement speed
     * @param maxRoamRange             The range of the target point that is randomly walked
     * @param frequency                How often the target point is updated
     * @param calNextTargetImmediately Whether to select the next target point immediately, regardless of the frequency of execution
     * @param runningTime              Maximum time to execute,-1 means no limit
     * @param avoidWater               Whether to walk away from water
     * @param maxRetryTime             Pick the maximum number of attempts at the target point
     */
    public FlatRandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime) {
        this.speed = speed;
        this.maxRoamRange = maxRoamRange;
        this.frequency = frequency;
        this.currentTargetCalTick = this.frequency;
        this.calNextTargetImmediately = calNextTargetImmediately;
        this.runningTime = runningTime;
        this.avoidWater = avoidWater;
        this.maxRetryTime = maxRetryTime;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (entity.canSit() && entity.isSitting()) {
            stop(entity);
            entity.getBehaviorGroup().setForceUpdateRoute(true);
            return false;
        }

        currentTargetCalTick++;
        durationTick++;
        if (entity.isEnablePitch()) entity.setEnablePitch(false);
        if (currentTargetCalTick >= frequency || (calNextTargetImmediately && needUpdateTarget(entity))) {
            Vector3 target = next(entity);
            if (avoidWater) {
                String blockId;
                int time = 0;
                while (time <= maxRetryTime && ((blockId = entity.level.getTickCachedBlock(target.add(0, -1, 0)).getId()) == Block.FLOWING_WATER || blockId == Block.WATER)) {
                    target = next(entity);
                    time++;
                }
            }
            if (entity.getMovementSpeed() != speed) entity.setMovementSpeed(speed);

            setRouteTarget(entity, target);
            setLookTarget(entity, target);
            currentTargetCalTick = 0;
            entity.getBehaviorGroup().setForceUpdateRoute(calNextTargetImmediately);
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

    protected boolean needUpdateTarget(EntityIntelligent entity) {
        return entity.getMoveTarget() == null;
    }

    protected Vector3 next(EntityIntelligent entity) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int z = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        return new Vector3(x, entity.y, z);
    }
}
