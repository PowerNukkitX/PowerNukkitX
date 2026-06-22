package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;

import java.util.concurrent.ThreadLocalRandom;

public class BreezeJumpExecutor implements EntityControl, IBehaviorExecutor {

    private long prepareTick = -1;

    @Override
    public boolean execute(EntityIntelligent entity) {
        long tick = entity.getLevel().getTick();
        if (tick % 80 == 0) {
            startSequence(entity);
            prepareTick = tick;
        } else {
            if (prepareTick != -1) {
                if (tick % 10 == 0) {
                    prepareTick = -1;
                    stopSequence(entity);
                }
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        entity.setEnablePitch(false);
        stopSequence(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setMovementSpeed(entity.getMovementSpeedDefault());
        entity.setEnablePitch(false);
        stopSequence(entity);
    }


    private void startSequence(Entity entity) {
        entity.setDataFlag(ActorFlags.JUMP_GOAL_JUMP);
    }

    private void stopSequence(Entity entity) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector3 motion = entity.getDirectionVector();
        motion.y = 0.6 + random.nextDouble(0.5f);
        entity.setMotion(motion);
        entity.setDataFlag(ActorFlags.JUMP_GOAL_JUMP, false);
        final ActorEventPacket pk = new ActorEventPacket();
        pk.setTargetRuntimeID(entity.getId());
        pk.setType(ActorEvent.GROUND_DUST);
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
