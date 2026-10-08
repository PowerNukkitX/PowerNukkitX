package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;


/**
 * Executes the warden digging behavior. It starts the digging animation, waits for the configured duration, and closes
 * or restores the entity when the behavior finishes or is interrupted.
 *
 * @author Curse
 */
public class WardenDiggingExecutor implements IBehaviorExecutor {

    protected final int duration;
    protected int endTick;

    /**
     * Creates a new WardenDiggingExecutor instance.
     *
     * @param duration value for this API
     */
    public WardenDiggingExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return entity.getLevel().getTick() < this.endTick;
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.endTick = entity.getLevel().getTick() + this.duration;
        entity.setMoveTarget(null);
        entity.setDataFlag(ActorFlags.DIGGING, true);
        entity.getLevel().addSound(entity, Sound.MOB_WARDEN_DIG);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.endTick = 0;
        entity.setDataFlag(ActorFlags.DIGGING, false);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.endTick = 0;
        entity.setDataFlag(ActorFlags.DIGGING, false);
        if (entity instanceof EntityWarden) entity.close();
    }
}
