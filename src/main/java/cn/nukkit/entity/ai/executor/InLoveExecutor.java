package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.network.protocol.EntityEventPacket;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class InLoveExecutor implements IBehaviorExecutor {

    protected int duration;
    protected int currentTick = 0;

    public InLoveExecutor(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        if (currentTick == 0) {
//            var memory = entity.getMemoryStorage().get(InLoveMemory.class);
//            memory.setData(Server.getInstance().getTick());
//            memory.setInLove(true);
            entity.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, Server.getInstance().getTick());
            entity.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, true);
        }
        currentTick++;
        if (currentTick > duration || !entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)/*interrupt by other*/) {
            currentTick = 0;
            entity.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, true);
            return false;
        }
        if (currentTick % 10 == 0) {
            sendLoveParticle(entity);
        }
        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, false);
        currentTick = 0;
    }

    protected void sendLoveParticle(EntityIntelligent entity) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.LOVE_PARTICLES;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
