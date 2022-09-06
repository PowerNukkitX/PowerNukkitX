package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.level.Sound;

@PowerNukkitXOnly
@Since("1.19.21-r4")
public class WardenSniffExecutor implements IBehaviorExecutor{

    protected int duration;//gt
    protected int endTime;

    public WardenSniffExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (Server.getInstance().getTick() >= this.endTime) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        this.endTime = Server.getInstance().getTick() + this.duration;
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SNIFFING, true);
        entity.level.addSound(entity.clone(), Sound.MOB_WARDEN_SNIFF);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SNIFFING, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_SNIFFING, false);
    }
}
