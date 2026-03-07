package cn.nukkit.entity.ai.executor;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.components.BoostableComponent;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;


/**
 * Ride control behavior that moves a rideable entity in the rider's
 * forward direction.
 *
 * Calculates a forward target based on the rider’s facing direction,
 * adjusts for simple vertical clearance, updates route and look targets,
 * and applies boost-based speed scaling when active.
 */
public class FollowRiderExecutor implements IBehaviorExecutor, EntityControl {
    public FollowRiderExecutor() { }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (!entity.isRideable()) return false;

        Entity riding = entity.getPassenger();
        if (riding == null) return false;

        Vector2 direction = riding.getDirectionPlane().normalize();
        Vector3 whereToGo = entity.asVector3f().asVector3().add(direction.x * 4.0f, 0, direction.y * 4.0f);

        for (int i = -1; i <= 1; i++) {
            Block block = entity.getLevel().getBlock(whereToGo.add(0, i, 0));
            if (block.canPassThrough()) {
                whereToGo = whereToGo.add(0, i, 0);
                break;
            }
        }

        setLookTarget(entity, whereToGo);
        setRouteTarget(entity, whereToGo);
        entity.getBehaviorGroup().setForceUpdateRoute(true);
        updateRideSpeed(entity);

        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        updateRideSpeed(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
    }

    protected void updateRideSpeed(EntityIntelligent entity) {
        float base = entity.getMovementSpeedDefault();

        BoostableComponent boostable = entity.getComponentBoostable();
        int ticks = entity.getBoostableTicks();

        boolean boosting = boostable != null && ticks > 0;
        @SuppressWarnings("null")
        float mult = boosting ? boostable.resolvedSpeedMultiplier() : 1.0f;
        final float BOOST_KNOB = 1.74f; // Parity knob: 1.74f that matches observed BDS feel
        float finalSpeed;
        if (boosting) {
            finalSpeed = (base * mult) + (base * BOOST_KNOB);
        } else finalSpeed = base;

        entity.setMovementSpeed(finalSpeed);
    }
}