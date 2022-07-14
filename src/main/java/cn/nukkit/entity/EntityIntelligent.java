package cn.nukkit.entity;

import cn.nukkit.entity.ai.EmptyBehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public abstract class EntityIntelligent extends EntityPhysical {

    public static final IBehaviorGroup EMPTY_BEHAVIOR_GROUP = new EmptyBehaviorGroup();

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public IBehaviorGroup getBehaviorGroup(){
        return EMPTY_BEHAVIOR_GROUP;
    };

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        getBehaviorGroup().tickRunningBehaviors(this);
        getBehaviorGroup().applyController(this);
        return true;
    }

    /**
     * 并行化以提高性能
     */
    @Override
    public void asyncPrepare(int currentTick) {
        super.asyncPrepare(currentTick);
        //No behavior group
        if (getBehaviorGroup() == null)
            return;
        IBehaviorGroup behaviorGroup = getBehaviorGroup();
        if (needsRecalcMovement) {
            getBehaviorGroup().collectSensorData(this);
            getBehaviorGroup().evaluateBehaviors(this);
            getBehaviorGroup().updateRoute(this);
        }
    }

    public IMemoryStorage getMemoryStorage(){
        return getBehaviorGroup().getMemory();
    }

    public float getJumpingHeight() {
        return 1.0f;
    }
}
