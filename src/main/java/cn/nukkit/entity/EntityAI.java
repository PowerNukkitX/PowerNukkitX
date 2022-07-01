package cn.nukkit.entity;

import cn.nukkit.entity.ai.message.BaseTickingMessage;
import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityAI extends EntityPhysical{

    public EntityAI(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public abstract BehaviorGroup getBehaviorGroup();

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        BehaviorGroup behaviorGroup = getBehaviorGroup();
        behaviorGroup.tickRunningBehaviors(this);
        behaviorGroup.message(this, new BaseTickingMessage());
        return true;
    }
}
