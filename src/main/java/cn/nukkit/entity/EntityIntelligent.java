package cn.nukkit.entity;

import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.route.SimpleAStarRouteFinder;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;
import java.util.Collections;

public abstract class EntityIntelligent extends EntityPhysical {

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Nullable
    public IBehaviorGroup getBehaviorGroup(){
        return null;
    };

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        //No behavior group
        if (getBehaviorGroup() == null)
            return true;
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
