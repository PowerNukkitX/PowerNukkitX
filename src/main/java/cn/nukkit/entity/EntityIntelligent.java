package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.entity.ai.behaviorgroup.EmptyBehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.EntityControlUtils;
import cn.nukkit.entity.ai.evaluator.LogicalUtils;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.memory.IMemoryStorage;
import cn.nukkit.entity.ai.memory.MemoryType;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.form.window.SimpleForm;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import org.cloudburstmc.nbt.NbtMap;

import java.util.Objects;

/**
 * {@code EntityIntelligent} abstracts an entity with a behavior group {@link IBehaviorGroup} (that is, with AI).
 */


public abstract class EntityIntelligent extends EntityPhysical implements LogicalUtils, EntityControlUtils {


    protected IBehaviorGroup behaviorGroup;

    /**
     * Is it an active entity? If the entity is inactive, the AI's operating frequency should be reduced.
     */
    @Getter
    protected boolean isActive = true;

    public EntityIntelligent(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        IMemoryStorage storage = getMemoryStorage();
        if (storage != null) {
            storage.put(CoreMemoryTypes.ENTITY_SPAWN_TIME, getLevel().getTick());
            MemoryType.getPersistentMemories().forEach(memory -> processMemoryStorage(storage, memory));
        }
    }

    @Override
    protected void initEntity() {
        this.behaviorGroup = requireBehaviorGroup();
        super.initEntity();
    }

    /**
     * Returns the behavior group {@link IBehaviorGroup} held by this entity. <p>
     * The default implementation will only return an empty behavior constant {@link EmptyBehaviorGroup}.
     * If you want the entity to have AI, you need to override this method.
     *
     * @return This entity holds the behavioral group
     */
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    /**
     * This method requests an instance of a behavior group; it is called when the entity initializes the behavior group.
     *
     * @return Newly created behavior group
     */
    protected IBehaviorGroup requireBehaviorGroup() {
        return new EmptyBehaviorGroup(this);
    }

    private <D> void processMemoryStorage(IMemoryStorage storage, MemoryType<D> mem) {
        var codec = mem.getCodec();
        var data = Objects.requireNonNull(codec).getDecoder().apply(this.namedTag);

        if (data != null) {
            storage.put(mem, data);
        }
    }

    @Override
    public void asyncPrepare(int currentTick) {
        if (!isAlive()) return;
        // Calculate whether it is active
        isActive = level.isHighLightChunk(getChunkX(), getChunkZ());
        if (!this.isImmobile()) { // immobile will disable physical AI.
            var behaviorGroup = getBehaviorGroup();
            if (behaviorGroup == null) return;
            behaviorGroup.collectSensorData(this);
            behaviorGroup.evaluateCoreBehaviors(this);
            behaviorGroup.evaluateBehaviors(this);
            behaviorGroup.tickRunningCoreBehaviors(this);
            behaviorGroup.tickRunningBehaviors(this);
            behaviorGroup.updateRoute(this);
            behaviorGroup.applyController(this);
            if (EntityAI.hasDebugOptions()) behaviorGroup.debugTick(this);
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
            storage.put(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, getLevel().getTick());
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

                var form = new SimpleForm("§f" + getOriginalName(), strBuilder.toString());
                form.send(player);
                return true;
            } else return super.onInteract(player, item, clickedPos);
        }
    }

    public IMemoryStorage getMemoryStorage() {
        return getBehaviorGroup().getMemoryStorage();
    }

    /**
     * Return the motion y that the entity should increase when jumping.
     *
     * @param jumpY Jump height
     * @return The entity needs to increase motion y
     */
    public double getJumpingMotion(double jumpY) {
        if (this.isTouchingWater()) {
//            if (jumpY > 0 && jumpY < 0.2) {
//                return 0.1;
//            } else if (jumpY < 0.51) {
//                return 0.1;
//            } else if (jumpY < 1.01) {
//                return 0.1;
//            } else {
//                return 0.1;
//            }
            return 0.1d;
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
