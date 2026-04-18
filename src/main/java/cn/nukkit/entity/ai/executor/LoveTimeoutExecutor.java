package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

/**
 * Handles the duration of the "in love" breeding state for entities.
 * <p>
 * Tracks how long the entity remains in love, periodically emits love
 * particles, and clears the love state and spouse memory when the
 * configured timeout expires.
 */
public class LoveTimeoutExecutor implements IBehaviorExecutor {

    protected final int loveTicks;

    public LoveTimeoutExecutor(int loveTicks) {
        this.loveTicks = loveTicks;
    }

    @Override
    public boolean execute(EntityIntelligent e) {
        if (!e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)) {
            if (e.getMemoryStorage().notEmpty(CoreMemoryTypes.ENTITY_SPOUSE)) {
                e.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);
            }
            return false;
        }

        int now = e.getLevel().getTick();

        if (e.getMemoryStorage().isEmpty(CoreMemoryTypes.LAST_IN_LOVE_TIME)) {
            e.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, now);
            return true;
        }

        int start = e.getMemoryStorage().get(CoreMemoryTypes.LAST_IN_LOVE_TIME);

        if (start > now) {
            e.getMemoryStorage().put(CoreMemoryTypes.LAST_IN_LOVE_TIME, now);
            return true;
        }

        int elapsed = now - start;

        if (elapsed % 10 == 0) {
            sendLoveParticle(e);
        }

        if (elapsed >= loveTicks) {
            e.getMemoryStorage().put(CoreMemoryTypes.IS_IN_LOVE, false);
            e.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);
            e.setDataFlag(ActorFlags.IN_LOVE, false);
            return false;
        }

        return true;
    }

    @Override
    public void onInterrupt(EntityIntelligent e) {
        if (e.getMemoryStorage().notEmpty(CoreMemoryTypes.ENTITY_SPOUSE)) {
            e.getMemoryStorage().clear(CoreMemoryTypes.ENTITY_SPOUSE);
        }
    }

    protected void sendLoveParticle(EntityIntelligent entity) {
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(entity.getId());
        pk.setType(ActorEvent.LOVE_HEARTS);
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
