package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.entity.ai.behaviorgroup.EmptyBehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.EntityControlUtils;
import cn.nukkit.entity.ai.evaluator.LogicalUtils;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

import java.util.Objects;

/**
 * {@code EntityIntelligent}抽象了一个具有行为组{@link IBehaviorGroup}（也就是具有AI）的实体
 */


public abstract class EntityIntelligent extends EntityPhysical implements LogicalUtils, EntityControlUtils {


    protected IBehaviorGroup behaviorGroup;

    /**
     * 是否为活跃实体，如果实体不活跃，就应当降低AI运行频率
     */
    @Getter
    protected boolean isActive = true;

    public EntityIntelligent(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        var storage = getMemoryStorage();
        if (storage != null) {
            storage.put(CoreMemoryTypes.ENTITY_SPAWN_TIME, Server.getInstance().getTick());
            MemoryType.getPersistentMemories().forEach(memory -> {
                var mem = (MemoryType<Object>) memory;
                var codec = mem.getCodec();
                var data = Objects.requireNonNull(codec).getDecoder().apply(this.namedTag);
                if (data != null) {
                    storage.put(mem, data);
                }
            });
        }
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.behaviorGroup = requireBehaviorGroup();
    }

    /**
     * 返回此实体持有的行为组{@link IBehaviorGroup} <br/>
     * 默认实现只会返回一个空行为{@link EmptyBehaviorGroup}常量，若你想让实体具有AI，你需要覆写此方法
     *
     * @return 此实体持有的行为组
     */
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    /**
     * 请求一个行为组实例，此方法在实体初始化行为组时调用
     *
     * @return 新创建的行为组
     */
    protected IBehaviorGroup requireBehaviorGroup() {
        return new EmptyBehaviorGroup(this);
    }

    @Override
    public void asyncPrepare(int currentTick) {
        if (!isAlive()) return;
        // 计算是否活跃
        isActive = level.isHighLightChunk(getChunkX(), getChunkZ());
        if (!this.isImmobile()) { // immobile会禁用实体AI
            var behaviorGroup = getBehaviorGroup();
            if (behaviorGroup == null) return;
            behaviorGroup.collectSensorData(this);
            behaviorGroup.evaluateCoreBehaviors(this);
            behaviorGroup.evaluateBehaviors(this);
            behaviorGroup.tickRunningCoreBehaviors(this);
            behaviorGroup.tickRunningBehaviors(this);
            behaviorGroup.updateRoute(this);
            behaviorGroup.applyController(this);
            if (EntityAI.checkDebugOption(EntityAI.DebugOption.BEHAVIOR)) behaviorGroup.debugTick(this);
        }
        super.asyncPrepare(currentTick);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        getBehaviorGroup().save(this);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var result = super.attack(source);
        var storage = getMemoryStorage();
        if (storage != null) {
            storage.put(CoreMemoryTypes.BE_ATTACKED_EVENT, source);
            storage.put(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, Server.getInstance().getTick());
        }
        return result;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (!EntityAI.checkDebugOption(EntityAI.DebugOption.MEMORY)) {
            return super.onInteract(player, item, clickedPos);
        } else {
            if (player.isOp() && Objects.equals(item.getId(), ItemID.STICK)) {
                var strBuilder = new StringBuilder();

                //Build memory information
                strBuilder.append("§eMemory:§f\n");
                var all = getMemoryStorage().getAll();
                all.forEach((memory, value) -> {
                    strBuilder.append(memory.getIdentifier());
                    strBuilder.append(" = §b");
                    strBuilder.append(value);
                    strBuilder.append("§f\n");
                });

                var form = new FormWindowSimple("§f" + getOriginalName(), strBuilder.toString());
                player.showFormWindow(form);
                return true;
            } else return super.onInteract(player, item, clickedPos);
        }
    }

    public IMemoryStorage getMemoryStorage() {
        return getBehaviorGroup().getMemoryStorage();
    }

    /**
     * 返回实体在跳跃时要增加的motion y
     *
     * @param jumpY 跳跃的高度
     * @return 实体要增加的motion y
     */
    public double getJumpingMotion(double jumpY) {
        if (this.isTouchingWater()) {
            if (jumpY > 0 && jumpY < 0.2) {
                return 0.25;
            } else if (jumpY < 0.51) {
                return 0.45;
            } else if (jumpY < 1.01) {
                return 0.6;
            } else {
                return 0.7;
            }
        } else {
            if (jumpY > 0 && jumpY < 0.2) {
                return 0.15;
            } else if (jumpY < 0.51) {
                return 0.35;
            } else if (jumpY < 1.01) {
                return 0.5;
            } else {
                return 0.6;
            }
        }
    }

    @Override
    public boolean enableHeadYaw() {
        return true;
    }
}
