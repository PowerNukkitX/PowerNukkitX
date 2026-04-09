package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.mob.EntityWarden;
import cn.nukkit.level.Sound;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;


public class WardenSniffExecutor implements IBehaviorExecutor {
    protected int angerAddition;
    protected int duration;//gt
    protected int endTime;

    public WardenSniffExecutor(int duration, int angerAddition) {
        this.duration = duration;
        this.angerAddition = angerAddition;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (entity.getLevel().getTick() >= this.endTime) {
            sniff(entity);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.endTime = entity.getLevel().getTick() + this.duration;
        entity.setDataFlag(ActorFlags.SNIFFING, true);
        entity.level.addSound(entity.clone(), Sound.MOB_WARDEN_SNIFF);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SNIFFING, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setDataFlag(ActorFlags.SNIFFING, false);
    }

    protected void sniff(EntityIntelligent entity) {
        if (!(entity instanceof EntityWarden warden)) return;
        for (var other : entity.level.getEntities()) {
            if (!warden.isValidAngerEntity(other, true)) continue;
            warden.addEntityAngerValue(other, this.angerAddition);
        }
    }
}
