package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.mob.EntityWarden;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;


public class WardenViolentAnimationExecutor implements IBehaviorExecutor {

    protected int duration;
    protected int currentTick;

    public WardenViolentAnimationExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (!(entity instanceof EntityWarden warden) || currentTick >= duration) return false;

        var target = warden.getRoarTarget();
        if (target == null) return false;
        entity.setLookTarget(target);
        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        this.currentTick = 0;
        entity.setDataFlag(ActorFlags.ROARING, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        entity.setMoveTarget(null);
        entity.setDataFlag(ActorFlags.ROARING, true);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        this.currentTick = 0;
        entity.setDataFlag(ActorFlags.ROARING, false);
        if (entity instanceof EntityWarden warden) warden.finishRoar();
    }
}
