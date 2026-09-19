package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.math.Vector3;


/**
 * Moves a warden toward a remembered suspicious location. The executor updates navigation and look control while the
 * memory exists, then clears movement state when the investigation ends.
 *
 * @author Curse
 */
public class WardenInvestigateSuspiciousLocationExecutor implements EntityControl, IBehaviorExecutor {

    private static final float BASE_MOVEMENT_SPEED = 0.3f;

    protected final float speed;
    protected final double goalRadiusSquared;
    protected Vector3 target;

    /**
     * Creates a new WardenInvestigateSuspiciousLocationExecutor instance.
     *
     * @param speed value for this API
     * @param goalRadius value for this API
     */
    public WardenInvestigateSuspiciousLocationExecutor(float speed, double goalRadius) {
        this.speed = speed;
        this.goalRadiusSquared = goalRadius * goalRadius;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (this.target == null || entity.distanceSquared(this.target) <= this.goalRadiusSquared) return false;
        setRouteTarget(entity, this.target);
        setLookTarget(entity, this.target);
        return true;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        if (!(entity instanceof EntityWarden warden)) return;

        this.target = warden.getSuspiciousLocation();
        if (this.target == null) return;

        entity.setMovementSpeed(this.speed);
        setRouteTarget(entity, this.target);
        setLookTarget(entity, this.target);
        entity.getBehaviorGroup().setForceUpdateRoute(true);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.finish(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.finish(entity);
    }

    private void finish(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setMovementSpeed(BASE_MOVEMENT_SPEED);

        if (entity instanceof EntityWarden warden && this.target != null) {
            warden.clearSuspiciousLocation(this.target);
        }
        this.target = null;
    }
}
