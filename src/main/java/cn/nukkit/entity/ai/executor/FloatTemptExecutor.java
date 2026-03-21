package cn.nukkit.entity.ai.executor;

import java.util.Set;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.data.EntityFlag;

/**
 * Floating variant of {@link TemptExecutor} for flying or levitating entities.
 *
 * Enables lift force while active and adjusts movement speed based on multiplier.
 * Restores previous lift and speed state when stopped.
 * 
 * @author Curse
 */
public class FloatTemptExecutor extends TemptExecutor {
    private static final float INTERNAL_FLYING_SPEED_TUNING = 8.0f;

    private Boolean previousLiftForce;
    private boolean hadPreviousSpeed;
    private float previousSpeed;

    public FloatTemptExecutor(float speedMultiplier, Set<String> temptItems) {
        super(speedMultiplier, temptItems);
    }

    public FloatTemptExecutor(boolean canTemptVertically, int withinRadius, float stopDistance, Set<String> temptItems) {
        super(
                1.0f,
                canTemptVertically,
                false,
                false,
                withinRadius,
                stopDistance,
                (TemptSound) null,
                temptItems
        );
    }

    public FloatTemptExecutor(float speedMultiplier, boolean canTemptVertically, int withinRadius, Set<String> temptItems) {
        super(
                speedMultiplier,
                canTemptVertically,
                false,
                false,
                withinRadius,
                2.0f,
                (TemptSound) null,
                temptItems
        );
    }

    public FloatTemptExecutor(float speedMultiplier, boolean canTemptVertically, int withinRadius, float stopDistance, Set<String> temptItems) {
        super(
                speedMultiplier,
                canTemptVertically,
                false,
                false,
                withinRadius,
                stopDistance,
                (TemptSound) null,
                temptItems
        );
    }

    public FloatTemptExecutor(float speedMultiplier, boolean canTemptVertically, boolean canTemptWhileRidden, boolean canGetScared, int withinRadius, float stopDistance, TemptSound temptSound, Set<String> temptItems) {
        super(
                speedMultiplier,
                canTemptVertically,
                canTemptWhileRidden,
                canGetScared,
                withinRadius,
                stopDistance,
                temptSound,
                temptItems
        );
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        try {
            previousLiftForce = entity.getMemoryStorage().get(CoreMemoryTypes.ENABLE_LIFT_FORCE);
        } catch (Throwable ignored) {
            previousLiftForce = null;
        }
        entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_LIFT_FORCE, true);

        previousSpeed = entity.getMovementSpeed();
        hadPreviousSpeed = true;

        super.onStart(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        super.onInterrupt(entity);
        restoreLift(entity);
        restoreSpeed(entity);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        super.onStop(entity);
        restoreLift(entity);
        restoreSpeed(entity);
    }

    private void restoreLift(EntityIntelligent entity) {
        try {
            if (previousLiftForce == null) {
                entity.getMemoryStorage().clear(CoreMemoryTypes.ENABLE_LIFT_FORCE);
            } else {
                entity.getMemoryStorage().put(CoreMemoryTypes.ENABLE_LIFT_FORCE, previousLiftForce);
            }
        } catch (Throwable ignored) {
        }
        previousLiftForce = null;
    }

    private void restoreSpeed(EntityIntelligent entity) {
        if (!hadPreviousSpeed) return;

        if (changedSpeed) {
            entity.setMovementSpeed(previousSpeed);
            changedSpeed = false;
        }

        hadPreviousSpeed = false;
    }

    @Override
    protected boolean shouldEnablePitchWhileTempting() {
        return true;
    }

    @Override
    protected float resolveDesiredSpeed(Entity entity) {
        return (entity.getDefaultFlyingSpeed() * INTERNAL_FLYING_SPEED_TUNING) * this.speedMultiplier;
    }

    @Override
    protected void clearTempt(EntityIntelligent entity) {
        removeRouteTarget(entity);
        removeLookTarget(entity);
        entity.setDataFlag(EntityFlag.TEMPTED, false);

        if (enabledPitch) {
            entity.setEnablePitch(false);
            enabledPitch = false;
        }

        oldTarget = null;
        lastTarget = null;
        nextSoundTick = -1;

        scaredFleeUntilTick = -1;
        scaredWaitUntilTick = -1;
        scaredFleeTarget = null;
        scaredFleeTargetFloor = null;
        nextRandomScareCheckTick = 0;

        lastScarePlayer = null;
        lastScareSampleTick = -1;
    }
}