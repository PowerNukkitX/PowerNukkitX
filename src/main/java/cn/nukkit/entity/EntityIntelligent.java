package cn.nukkit.entity;

import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Collections;

public abstract class EntityIntelligent extends EntityPhysical{

    private final BehaviorGroup behaviorGroup = new BehaviorGroup(Collections.EMPTY_SET,Collections.EMPTY_SET);

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
        behaviorGroup.tick(this);
        return true;
    }

    public IMemoryStorage getMemory(){
        return getBehaviorGroup().getMemory();
    }
}
