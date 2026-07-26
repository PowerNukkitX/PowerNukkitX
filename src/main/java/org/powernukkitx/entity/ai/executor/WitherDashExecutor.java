package org.powernukkitx.entity.ai.executor;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;

public class WitherDashExecutor extends MoveToTargetExecutor {

    protected int tick = 0;
    protected MemoryType<? extends Entity> targetMemory;

    public WitherDashExecutor(MemoryType<? extends Vector3> memory, float speed, boolean updateRouteImmediatelyWhenTargetChange, float maxFollowRange, float minFollowRange) {
        super(memory, speed, updateRouteImmediatelyWhenTargetChange, maxFollowRange, minFollowRange, false);
    }

    @Override
    public void onStart(EntityIntelligent entity) {
        super.onStart(entity);
        entity.getMemoryStorage().put(CoreMemoryTypes.LAST_ATTACK_DASH, entity.getLevel().getTick());
        entity.setDataFlag(ActorFlags.CAN_DASH);
    }

    @Override
    public void onStop(EntityIntelligent entity) {
        super.onStop(entity);
        entity.setDataFlag(ActorFlags.CAN_DASH, false);
    }

    @Override
    public void onInterrupt(EntityIntelligent entity) {
        super.onInterrupt(entity);
        entity.setDataFlag(ActorFlags.CAN_DASH, false);
    }
}
