package cn.nukkit.entity;

import cn.nukkit.entity.ai.message.BaseTickingMessage;
import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityIntelligent extends EntityPhysical{

    private final BehaviorGroup behaviorGroup = new BehaviorGroup();

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public BehaviorGroup getBehaviorGroup(){
        return behaviorGroup;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        BehaviorGroup behaviorGroup = getBehaviorGroup();
        behaviorGroup.tickRunningBehaviors(this);
        behaviorGroup.message(this, new BaseTickingMessage());
        return true;
    }
}
