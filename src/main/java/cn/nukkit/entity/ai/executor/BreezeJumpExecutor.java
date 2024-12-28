package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.concurrent.ThreadLocalRandom;

public class BreezeJumpExecutor implements EntityControl, IBehaviorExecutor {

    private long prepareTick = -1;

    @Override
    public boolean execute(EntityIntelligent entity) {
        long tick = entity.getLevel().getTick();
        if(tick % 80 == 0) {
            startSequence(entity);
            prepareTick = tick;
        } else {
            if(prepareTick != -1) {
                if(tick % 10 == 0) {
                    prepareTick = -1;
                    stopSequence(entity);
                }
            }
        }
        return true;
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        entity.setEnablePitch(false);
        stopSequence(entity);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        entity.setMovementSpeed(EntityLiving.DEFAULT_SPEED);
        entity.setEnablePitch(false);
        stopSequence(entity);
    }



    private void startSequence(Entity entity) {
        entity.setDataFlag(EntityFlag.JUMP_GOAL_JUMP);
    }

    private void stopSequence(Entity entity) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vector3 motion = entity.getDirectionVector();
        motion.y = 0.6 + random.nextDouble(0.5f);
        entity.setMotion(motion);
        entity.setDataFlag(EntityFlag.JUMP_GOAL_JUMP, false);
        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = entity.getId();
        pk.event = EntityEventPacket.DUST_PARTICLES;
        Server.broadcastPacket(entity.getViewers().values(), pk);
    }
}
