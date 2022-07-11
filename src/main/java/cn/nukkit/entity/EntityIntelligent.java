package cn.nukkit.entity;

import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Collections;

public abstract class EntityIntelligent extends EntityPhysical{

    private final IBehaviorGroup behaviorGroup = new BehaviorGroup(Collections.EMPTY_SET,Collections.EMPTY_SET,Collections.EMPTY_SET,null);

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public IBehaviorGroup getBehaviorGroup(){
        return behaviorGroup;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        return true;
    }

    /**
     * 并行化以提高性能
     */
    @Override
    public void asyncPrepare(int currentTick) {
        super.asyncPrepare(currentTick);
        IBehaviorGroup behaviorGroup = getBehaviorGroup();
        behaviorGroup.collectSensorData(this);
        behaviorGroup.evaluateBehaviors(this);
        behaviorGroup.updateRoute(this);
        behaviorGroup.tickRunningBehaviors(this);
        behaviorGroup.applyController(this);
    }

    public IMemoryStorage getMemoryStorage(){
        return getBehaviorGroup().getMemory();
    }

    public float getJumpingHeight() {
        return 1.0f;
    }
}
