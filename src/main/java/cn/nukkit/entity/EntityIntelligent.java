package cn.nukkit.entity;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.ai.behaviorgroup.EmptyBehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.memory.AttackMemory;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * {@code EntityIntelligent}抽象了一个具有行为组{@link IBehaviorGroup}（也就是具有AI）的实体
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
@Setter
public abstract class EntityIntelligent extends EntityPhysical {

    public static final IBehaviorGroup EMPTY_BEHAVIOR_GROUP = new EmptyBehaviorGroup();

    /**
     * 是否为活跃实体，如果实体不活跃，就应当降低AI运行频率
     */
    protected boolean isActive = true;

    //我们将寻路相关的参数直接作为属性存储到EntityIntelligent中，这样可以提高性能
    protected Vector3 lookTarget;
    protected Vector3 moveTarget;
    protected Vector3 moveDirectionStart;
    protected Vector3 moveDirectionEnd;
    protected boolean needUpdateMoveDirection;

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
    }

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        if ((currentTick & 255) == 0) { // 每256刻才会计算一次是否为突出区块
            isActive = level.isHighLightChunk(getFloorX(), getFloorZ());
        }
        var behaviorGroup = getBehaviorGroup();
        behaviorGroup.tickRunningCoreBehaviors(this);
        behaviorGroup.tickRunningBehaviors(this);
        behaviorGroup.applyController(this);
        return true;
    }

    /**
     * 我们将行为组运行循环的部分工作并行化以提高性能
     */
    @Override
    public void asyncPrepare(int currentTick) {
        super.asyncPrepare(currentTick);
        var behaviorGroup = getBehaviorGroup();
        //No behavior group
        if (behaviorGroup == null)
            return;
        if (needsRecalcMovement) {
            behaviorGroup.collectSensorData(this);
            behaviorGroup.evaluateCoreBehaviors(this);
            behaviorGroup.evaluateBehaviors(this);
            behaviorGroup.updateRoute(this);
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var result = super.attack(source);
        var memory = getMemoryStorage();
        if (memory != null) {
            memory.get(AttackMemory.class).setData(source);
        }
        return result;
    }

    @Nullable
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

    public boolean hasMoveDirection(){
        return moveDirectionStart != null && moveDirectionEnd != null;
    }
}
