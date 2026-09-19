package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.mob.EntityWarden;
import org.powernukkitx.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;


public class WardenEmergingAnimationExecutor implements IBehaviorExecutor {

    protected int duration;
    protected int currentTick;

    public WardenEmergingAnimationExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        return currentTick <= duration;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.currentTick = 0;
        if (entity instanceof EntityWarden warden) warden.finishEmerging();
        else entity.setDataFlag(ActorFlags.EMERGING, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.getLevel().addSound(entity, Sound.MOB_WARDEN_EMERGE);
        entity.setMoveTarget(null);
        entity.setDataFlag(ActorFlags.EMERGING, true);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.currentTick = 0;
        if (entity instanceof EntityWarden warden) warden.finishEmerging();
        else entity.setDataFlag(ActorFlags.EMERGING, false);
    }
}
