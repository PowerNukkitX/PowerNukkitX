package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.network.protocol.EntityEventPacket;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class InLoveExecutor implements IBehaviorExecutor {

    protected int currentTick = 0;

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (currentTick % 10 == 0){
            sendLoveParticle(entity);
        }
        return false;
    }

    protected void sendLoveParticle(EntityIntelligent entity){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.LOVE_PARTICLES;
        Server.broadcastPacket(entity.getViewers().values(),pk);
    }
}
