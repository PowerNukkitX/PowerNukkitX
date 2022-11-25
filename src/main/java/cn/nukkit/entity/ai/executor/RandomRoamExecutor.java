package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class RandomRoamExecutor implements EntityControl, IBehaviorExecutor {

    protected float speed;
    protected int maxRoamRange;
    protected int frequency;

    protected int currentTargetCalTick = 0;
    protected int durationTick = 0;
    protected boolean calNextTargetImmediately = false;
    protected int runningTime;
    protected boolean avoidWater;
    protected int maxRetryTime;

    public RandomRoamExecutor(float speed, int maxRoamRange, int frequency) {
        this(speed, maxRoamRange, frequency, false, 100);
    }

    public RandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, false, 10);
    }

    public RandomRoamExecutor(float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime) {
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
        currentTargetCalTick++;
        durationTick++;
        if (currentTargetCalTick >= frequency || (calNextTargetImmediately && needUpdateTarget(entity))) {
            Vector3 target = next(entity);
            if (avoidWater) {
                int blockId;
                int time = 0;
                while (time <= maxRetryTime && ((blockId = entity.level.getTickCachedBlock(target.add(0, -1, 0)).getId()) == Block.FLOWING_WATER || blockId == Block.STILL_WATER)) {
                    target = next(entity);
                    time++;
                }
            }
            if (entity.getMovementSpeed() != speed)
                entity.setMovementSpeed(speed);
            //更新寻路target
            setRouteTarget(entity, target);
            //更新视线target
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
        removeRouteTarget(entity);
        removeLookTarget(entity);
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
        double y = entity.getLevel().getHighestBlockAt(x, z) + 1;
        return new Vector3(x, y, z);
    }
}
