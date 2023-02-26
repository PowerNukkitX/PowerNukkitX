package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
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
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;

import java.util.Objects;

/**
 * {@code EntityIntelligent}抽象了一个具有行为组{@link IBehaviorGroup}（也就是具有AI）的实体
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class EntityIntelligent extends EntityPhysical implements LogicalUtils, EntityControlUtils {

    public static final IBehaviorGroup EMPTY_BEHAVIOR_GROUP = new EmptyBehaviorGroup();

    @Since("1.19.60-r1")
    protected IBehaviorGroup behaviorGroup;

    /**
     * 是否为活跃实体，如果实体不活跃，就应当降低AI运行频率
     */
    @Getter
    protected boolean isActive = true;

    public EntityIntelligent(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.behaviorGroup = requireBehaviorGroup();
        var storage = getMemoryStorage();
        if (storage != null) {
            storage.put(CoreMemoryTypes.ENTITY_SPAWN_TIME, Server.getInstance().getTick());
            MemoryType.getPersistentMemories().forEach(memory -> {
                var mem = (MemoryType<Object>) memory;
                var codec = mem.getCodec();
                var data = Objects.requireNonNull(codec).getDecoder().apply(this.namedTag);
                if (data != null) {
                    storage.put(mem, data);
                    codec.init(data, this);
                }
            });
        }
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
    @Since("1.19.60-r1")
    protected IBehaviorGroup requireBehaviorGroup() {
        return EMPTY_BEHAVIOR_GROUP;
    }

    @Override
    public void asyncPrepare(int currentTick) {
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
            if (player.isOp() && item.getId() == ItemID.STICK) {
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
     * 返回实体最大的跳跃高度，返回值会用在移动处理上
     *
     * @return 实体最大跳跃高度
     */
    public float getJumpingHeight() {
        return 1.0f;
    }

    @Override
    public boolean enableHeadYaw() {
        return true;
    }
}
