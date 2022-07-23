package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.LookTargetMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
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
                while (time <= maxRetryTime && ((blockId = entity.level.getBlock(target.add(0, -1, 0)).getId()) == Block.FLOWING_WATER || blockId == Block.STILL_WATER)) {
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
        return !entity.getMemoryStorage().contains(MoveTargetMemory.class);
    }

    protected Vector3 next(EntityIntelligent entity){
        //随机计算下一个落点
        Vector3 next = this.nextXZ(entity.getX(), entity.getY(), maxRoamRange);
        next.y = entity.getLevel().getHighestBlockAt(next.getFloorX(), next.getFloorZ()) + 1;
        return next;
    }

    protected Vector3 nextXZ(double centerX, double centerZ, int maxRange) {
        Vector3 vec3 = new Vector3(centerX, 0, centerZ);
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRange * 2) - maxRange;
        int z = random.nextInt(maxRange * 2) - maxRange;
        return vec3;
    }

    protected void setRouteTarget(@NotNull EntityIntelligent entity, Vector3 vector3) {
        entity.getMemoryStorage().put(new MoveTargetMemory(vector3));
    }

    protected void setLookTarget(@NotNull EntityIntelligent entity, Vector3 vector3){
        entity.getMemoryStorage().put(new LookTargetMemory(vector3));
    }

    protected void removeRouteTarget(@NotNull EntityIntelligent entity) {
        entity.getMemoryStorage().remove(MoveTargetMemory.class);
    }

    protected void removeLookTarget(@NotNull EntityIntelligent entity){
        entity.getMemoryStorage().remove(LookTargetMemory.class);
    }
}
