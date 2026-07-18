package org.powernukkitx.entity.ai.executor.enderdragon;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.executor.EntityControl;
import org.powernukkitx.entity.ai.executor.IBehaviorExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.item.EntityEnderCrystal;
import org.powernukkitx.entity.mob.EntityEnderDragon;
import org.powernukkitx.math.Vector2;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


@Getter
public class CircleMovementExecutor implements EntityControl, IBehaviorExecutor {

    //indicates which Memory the executor should get the target position from
    protected MemoryType<? extends Vector3> memory;
    protected float speed;
    protected boolean updateRouteImmediatelyWhenTargetChange;

    private int sections;
    private int size;

    private int circles;

    private int startLoc = 0;
    private int circleLoc = 0;

    private Vector3 lastLocation = null;

    private int ticks = 0;


    public CircleMovementExecutor(MemoryType<? extends Vector3> memory, float speed,  int size, int sections, int circles) {
        this(memory, speed, false, size, sections, circles);
    }

    public CircleMovementExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange, int size, int sections, int circles) {
        this.memory = memory;
        this.speed = speed;
        this.updateRouteImmediatelyWhenTargetChange = updateRouteImmediatelyWhenTargetChange;
        this.size = size;
        this.sections = sections;
        this.circles = circles;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        if (entity.isEnablePitch()) entity.setEnablePitch(false);
        if (needUpdateTarget(entity)) {
            this.circleLoc++;
            Vector3 target = next(entity);
            lastLocation = target;
            if (entity.getMovementSpeed() != speed)
                entity.setMovementSpeed(speed);
            entity.getBehaviorGroup().setForceUpdateRoute(updateRouteImmediatelyWhenTargetChange);
        }
        setRouteTarget(entity, lastLocation);
        setLookTarget(entity, lastLocation);
        return circleLoc < circles;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        startLoc = Utils.rand(0, sections);
        circleLoc = 0;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        stop(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        stop(entity);
        if(entity instanceof EntityEnderDragon) {
            if(Utils.rand(0, 3 + (int) Arrays.stream(entity.getLevel().getEntities()).filter(entity1 -> entity1 instanceof EntityEnderCrystal && entity1.toHorizontal().distance(Vector2.ZERO) < 128).count()) < 1) {
                entity.getMemoryStorage().put(CoreMemoryTypes.FORCE_PERCHING, true);
            }
        }
    }

    protected void stop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setEnablePitch(true);
    }

    protected boolean needUpdateTarget(EntityIntelligent entity) {
        entity.recalculateBoundingBox(false);
        return lastLocation == null || entity.getBoundingBox().grow(10, 10, 10).isVectorInside(lastLocation);
    }

    protected Vector3 next(EntityIntelligent entity) {
        Vector3 origin = entity.getBehaviorGroup().getMemoryStorage().get(memory);
        double angleIncrement = 360.0 / sections;
        double angle = Math.toRadians(((circleLoc + startLoc) * angleIncrement));
        double particleX = origin.getX() + Math.cos(angle) * size;
        double particleZ = origin.getZ() + Math.sin(angle) * size;
        return new Vector3(particleX, origin.y + Utils.rand(-5, 7), particleZ);
    }
}
