package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.InLoveMemory;
import cn.nukkit.network.protocol.EntityEventPacket;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class InLoveExecutor implements IBehaviorExecutor {

    protected int maxTick;
    protected int currentTick = 0;

    public InLoveExecutor(int maxTick) {
        this.maxTick = maxTick;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        currentTick++;
        if (currentTick > maxTick){
            updateInLoveMemory(entity);
            return false;
        }
        if (currentTick % 10 == 0){
            sendLoveParticle(entity);
        }
        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        updateInLoveMemory(entity);
        currentTick = 0;
    }

    protected void sendLoveParticle(EntityIntelligent entity){
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.LOVE_PARTICLES;
        Server.broadcastPacket(entity.getViewers().values(),pk);
    }

    protected void updateInLoveMemory(EntityIntelligent entity){
        var memory = entity.getMemoryStorage().get(InLoveMemory.class);
        memory.setData(Server.getInstance().getTick());
    }
}
