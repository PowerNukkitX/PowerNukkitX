package cn.nukkit.entity;

import cn.nukkit.entity.ai.IBehaviorGroup;
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

    public IBehaviorGroup getBehaviorGroup(){
        return behaviorGroup;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        IBehaviorGroup behaviorGroup = getBehaviorGroup();
        behaviorGroup.tickRunningBehaviors(this);
        return true;
    }

    /**
     * 我们将传感器数据的收集和行为评估工作并行化以提高性能
     */
    @Override
    public void asyncPrepare(int currentTick) {
        super.asyncPrepare(currentTick);
        IBehaviorGroup behaviorGroup = getBehaviorGroup();
        behaviorGroup.collectSensorData(this);
        behaviorGroup.evaluateBehaviors(this);
    }

    public IMemoryStorage getMemory(){
        return getBehaviorGroup().getMemory();
    }

    public float getJumpingHeight() {
        return 1.0f;
    }
}
