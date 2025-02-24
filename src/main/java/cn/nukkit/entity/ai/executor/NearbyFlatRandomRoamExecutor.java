package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;


public class NearbyFlatRandomRoamExecutor extends FlatRandomRoamExecutor {

    protected MemoryType<? extends Vector3> memory;

    public NearbyFlatRandomRoamExecutor(MemoryType<? extends Vector3> memory, float speed, int maxRoamRange, int frequency) {
        this(memory, speed, maxRoamRange, frequency, false, 100);
    }

    public NearbyFlatRandomRoamExecutor(MemoryType<? extends Vector3> memory, float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime) {
        this(memory, speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, false, 10);
    }

    public NearbyFlatRandomRoamExecutor(MemoryType<? extends Vector3> memory, float speed, int maxRoamRange, int frequency, boolean calNextTargetImmediately, int runningTime, boolean avoidWater, int maxRetryTime) {
        super(speed, maxRoamRange, frequency, calNextTargetImmediately, runningTime, avoidWater, maxRetryTime);
        this.memory = memory;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        currentTargetCalTick++;
        durationTick++;

        Vector3 center = entity.getMemoryStorage().get(memory);
        if(center == null) return false;

        if (entity.isEnablePitch()) entity.setEnablePitch(false);
        if (currentTargetCalTick >= frequency || (calNextTargetImmediately && needUpdateTarget(entity))) {
            Vector3 target = next(center);
            if (avoidWater) {
                String blockId;
                int time = 0;
                while (time <= maxRetryTime && ((blockId = entity.level.getTickCachedBlock(target.add(0, -1, 0)).getId()) == Block.FLOWING_WATER || blockId == Block.WATER)) {
                    target = next(center);
                    time++;
                }
            }
            if (entity.getMovementSpeed() != speed)
                entity.setMovementSpeed(speed);

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

    protected Vector3 next(Vector3 entity) {
        var random = ThreadLocalRandom.current();
        int x = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorX();
        int z = random.nextInt(maxRoamRange * 2) - maxRoamRange + entity.getFloorZ();
        return new Vector3(x, entity.y, z);
    }
}
