package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import org.jetbrains.annotations.NotNull;


/**
 * Makes a rideable entity follow the movement or combat target of its rider.
 *
 * If the rider has an attack target, the entity moves toward it; otherwise,
 * it follows the rider's current movement target. Updates look and route
 * targets and optionally refreshes pathfinding when the destination changes.
 */
public class MoveToRiderTargetExecutor implements EntityControl, IBehaviorExecutor {
    protected final float speed;
    protected final boolean updateRouteImmediatelyWhenTargetChange;
    protected Vector3 oldTargetFloor;

    public MoveToRiderTargetExecutor(float speed, boolean updateRouteImmediatelyWhenTargetChange) {
        this.speed = speed;
        this.updateRouteImmediatelyWhenTargetChange = updateRouteImmediatelyWhenTargetChange;
    }

    @Override
    public boolean execute(@NotNull EntityIntelligent entity) {
        Entity riderRaw = entity.getRider();
        if (riderRaw == null) return false;
        if (riderRaw instanceof Player) return false;
        if (!(riderRaw instanceof EntityIntelligent rider)) return false;

        // Priority attack target
        if (rider.targetEntity != null) {
            if (rider.targetEntity.level != entity.level) return false;

            Vector3 lookTarget = rider.targetEntity.getLocation().add(0.0f, rider.targetEntity.getEyeHeight(), 0.0f);
            return drive(entity, rider.targetEntity.getPosition(), lookTarget, true);
        }

        // Fallback to rider roam target
        Vector3 riderMoveTarget = rider.getMoveTarget();
        if (riderMoveTarget == null) return false;

        Position routeTarget = toPosition(entity, riderMoveTarget);
        if (routeTarget == null) return false;

        Vector3 lookTarget = routeTarget;

        return drive(entity, routeTarget, lookTarget, false);
    }

    private Position toPosition(EntityIntelligent vehicle, Vector3 v) {
        if (v == null) return null;

        if (v instanceof Position p) {
            if (p.level == null) return null;
            if (vehicle.level != null && p.level != vehicle.level) return null;
            return p;
        }

        if (vehicle.level == null) return null;
        return new Position(v.x, v.y, v.z, vehicle.level);
    }

    private boolean drive(EntityIntelligent entity, Position routeTarget, Vector3 lookTarget, boolean combat) {
        if (!entity.isEnablePitch()) entity.setEnablePitch(true);
        if (combat && entity.getMovementSpeed() != speed) {
            entity.setMovementSpeed(speed);
        } else if (!combat && entity.getMovementSpeed() != entity.getMovementSpeedDefault()) {
            entity.setMovementSpeed(entity.getMovementSpeedDefault());
        }

        setRouteTarget(entity, routeTarget);
        setLookTarget(entity, lookTarget);

        if (updateRouteImmediatelyWhenTargetChange) {
            Vector3 floor = routeTarget.floor();
            if (oldTargetFloor == null || !oldTargetFloor.equals(floor)) {
                entity.getBehaviorGroup().setForceUpdateRoute(true);
                oldTargetFloor = floor;
            }
        }

        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        entity.setEnablePitch(false);
        oldTargetFloor = null;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        entity.setEnablePitch(false);
        oldTargetFloor = null;
    }
}
