package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class RandomRoamExecutor implements IBehaviorExecutor{

    protected float speed;
    protected int maxRoamRange;
    protected int frequency;

    protected int currentTargetCalTick = 0;
    protected boolean calNextTargetImmediately = false;
    protected boolean keepRunning;
    protected boolean avoidWater;
    protected int maxRetryTime;

    public RandomRoamExecutor(float speed,int maxRoamRange, int frequency){
        this(speed,maxRoamRange,frequency,false,false);
    }

    public RandomRoamExecutor(float speed,int maxRoamRange, int frequency, boolean calNextTargetImmediately, boolean keepRunning){
        this(speed,maxRoamRange,frequency,calNextTargetImmediately,keepRunning,false,10);
    }

    public RandomRoamExecutor(float speed,int maxRoamRange, int frequency, boolean calNextTargetImmediately, boolean keepRunning, boolean avoidWater, int maxRetryTime) {
        this.speed = speed;
        this.maxRoamRange = maxRoamRange;
        this.frequency = frequency;
        this.currentTargetCalTick = this.frequency;
        this.calNextTargetImmediately = calNextTargetImmediately;
        this.keepRunning = keepRunning;
        this.avoidWater = avoidWater;
        this.maxRetryTime = maxRetryTime;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        currentTargetCalTick++;
        if (currentTargetCalTick >= frequency || (calNextTargetImmediately && needUpdateTarget(entity))) {
            //roam to a random location
            var random = ThreadLocalRandom.current();
            Vector3 target = next(entity);
            if (avoidWater) {
                int blockId = -1;
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
        //下一gt是否执行取决于评估器的结果
        return keepRunning;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        currentTargetCalTick = frequency;
    }

    protected boolean needUpdateTarget(EntityIntelligent entity){
        return entity.getMoveTarget() == null;
    }

    protected Vector3 next(EntityIntelligent entity){
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int z = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        double y = entity.getLevel().getHighestBlockAt(x, z) + 1;
        return new Vector3(x, y, z);
    }

    protected void setRouteTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.setMoveTarget(vector3);
    }

    protected void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3){
        entity.setLookTarget(vector3);
    }

    protected void removeRouteTarget(@NotNull EntityIntelligent entity) {
        entity.setMoveTarget(null);
    }

    protected void removeLookTarget(@NotNull EntityIntelligent entity){
        entity.setLookTarget(null);
    }
}
