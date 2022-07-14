package cn.nukkit.entity;

import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Collections;

public abstract class EntityIntelligent extends EntityPhysical {
    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public abstract IBehaviorGroup getBehaviorGroup();

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
