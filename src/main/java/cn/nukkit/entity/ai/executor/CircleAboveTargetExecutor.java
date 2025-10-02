package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


@Getter
public class CircleAboveTargetExecutor implements EntityControl, IBehaviorExecutor {

    //指示执行器应该从哪个Memory获取目标位置
    protected MemoryType<? extends Vector3> memory;
    protected float speed;
    protected Vector3 oldTarget;
    protected boolean updateRouteImmediatelyWhenTargetChange;
    protected boolean enableRangeTest = false;
    protected boolean clearDataWhenLose;

    int sections = 8;

    private int circleLoc = 0;


    public CircleAboveTargetExecutor(MemoryType<? extends Vector3> memory, float speed) {
        this(memory, speed, false);
    }

    public CircleAboveTargetExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange) {
        this(memory, speed, updateRouteImmediatelyWhenTargetChange, false);
    }

    public CircleAboveTargetExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange,boolean clearDataWhenLose) {
        this.memory = memory;
        this.speed = speed;
        this.updateRouteImmediatelyWhenTargetChange = updateRouteImmediatelyWhenTargetChange;
        this.clearDataWhenLose = clearDataWhenLose;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (entity.getBehaviorGroup().getMemoryStorage().isEmpty(memory)) {
            return false;
        }
        Vector3 target = entity.getBehaviorGroup().getMemoryStorage().get(memory).clone();
        Location origin = entity.getBehaviorGroup().getMemoryStorage().get(CoreMemoryTypes.LAST_ATTACK_ENTITY).add(0, 24, 0);
        double angleIncrement = 360.0 / sections;
        double angle = Math.toRadians((circleLoc * angleIncrement));
        double particleX = origin.getX() + Math.cos(angle) * 20;
        double particleZ = origin.getZ() + Math.sin(angle) * 20;
        Location loc = new Location(particleX, origin.y, particleZ, angle, 0, origin.level);
        if(entity.distance(loc) < 3) {
            circleLoc++;
            circleLoc%=8;
        }
        setRouteTarget(entity, loc);
        setLookTarget(entity, loc);
        if (updateRouteImmediatelyWhenTargetChange) {
            var floor = target.floor();

            if (oldTarget == null || oldTarget.equals(floor))
                entity.getBehaviorGroup().setForceUpdateRoute(true);

            oldTarget = floor;
        }

        if (entity.getMovementSpeed() != speed)
            entity.setMovementSpeed(speed);

        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_PITCH, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setMovementSpeed(EntityIntelligent.DEFAULT_SPEED);
        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_PITCH, true);
        entity.setEnablePitch(false);
        if (clearDataWhenLose)
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        circleLoc++;
        circleLoc%=8;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getDefaultSpeed());
        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_PITCH, true);
        entity.setEnablePitch(false);
        if (clearDataWhenLose)
            entity.getBehaviorGroup().getMemoryStorage().clear(memory);
        circleLoc++;
        circleLoc%=8;
    }
}
