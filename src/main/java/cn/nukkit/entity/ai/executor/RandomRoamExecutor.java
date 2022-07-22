package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.LookTargetMemory;
import cn.nukkit.entity.ai.memory.MoveTargetMemory;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class RandomRoamExecutor implements IBehaviorExecutor{

    protected float speed;
    protected int randomTargetRange;
    protected int frequency;

    protected int currentTargetCalTick = 0;

    public RandomRoamExecutor(float speed, int randomTargetRange, int frequency) {
        this.speed = speed;
        this.randomTargetRange = randomTargetRange;
        this.frequency = frequency;
        this.currentTargetCalTick = this.frequency;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        //获取目标位置（这个clone很重要）
        Vector3 target = randomTarget(entity);
        if (target != null) {
            if (entity.getMovementSpeed() != speed)
                entity.setMovementSpeed(speed);
            //更新寻路target
            setRouteTarget(entity, target);
            //更新视线target
            setLookTarget(entity, target);
        }
        return false;
    }

    @Nullable
    protected Vector3 randomTarget(EntityIntelligent entity){
        currentTargetCalTick++;
        if (currentTargetCalTick >= frequency) {
            //roam to a random location
            var random = ThreadLocalRandom.current();
            Vector3 target = entity.getPosition().add(random.nextInt(randomTargetRange * 2) - randomTargetRange, 0, random.nextInt(randomTargetRange * 2) - randomTargetRange);
            currentTargetCalTick = 0;
            return target;
        }
        return null;
    }

    protected Vector3 next(EntityIntelligent entity){
        //随机计算下一个落点
        Vector3 next = this.nextXZ(entity.getX(), entity.getY(), randomTargetRange);
        next.y = entity.getLevel().getHighestBlockAt(next.getFloorX(), next.getFloorZ()) + 1;
        return next;
    }

    protected Vector3 nextXZ(double centerX, double centerZ, int maxRange) {
        Vector3 vec3 = new Vector3(centerX, 0, centerZ);
        var random = ThreadLocalRandom.current();
        vec3.x = Math.round(vec3.x) + random.nextInt(-maxRange, maxRange) + 0.5;
        vec3.z = Math.round(vec3.z) + random.nextInt(-maxRange, maxRange) + 0.5;
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
