package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behaviorgroup.EmptyBehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.memory.AttackMemory;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * {@code EntityIntelligent}抽象了一个具有行为组{@link IBehaviorGroup}（也就是具有AI）的实体
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityIntelligent extends EntityPhysical {

    public static final IBehaviorGroup EMPTY_BEHAVIOR_GROUP = new EmptyBehaviorGroup();

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * 返回此实体持有的行为组{@link IBehaviorGroup} <br/>
     * 默认实现只会返回一个空行为{@link EmptyBehaviorGroup}常量，若你想让实体具有AI，你需要覆写此方法
     *
     * @return 此实体持有的行为组
     */
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
     * 我们将行为组运行循环的部分工作并行化以提高性能
     */
    @Override
    public void asyncPrepare(int currentTick) {
        super.asyncPrepare(currentTick);
        //No behavior group
        if (getBehaviorGroup() == null)
            return;
        if (needsRecalcMovement) {
            getBehaviorGroup().collectSensorData(this);
            getBehaviorGroup().evaluateBehaviors(this);
            getBehaviorGroup().updateRoute(this);
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var result = super.attack(source);
        getMemoryStorage().put(new AttackMemory(source));
        return result;
    }

    public IMemoryStorage getMemoryStorage(){
        return getBehaviorGroup().getMemoryStorage();
    }

    /**
     * 返回实体最大的跳跃高度，返回值会用在移动处理上
     * @see WalkController
     *
     * @return 实体最大跳跃高度
     */
    public float getJumpingHeight() {
        return 1.0f;
    }
}
