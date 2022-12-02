package cn.nukkit.entity.passive;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AllMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.ConditionalProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

import java.util.Set;

import static cn.nukkit.entity.mob.EntityMob.DIFFICULTY_HAND_DAMAGE;

public class EntityCat extends EntityWalkingAnimal {
    public static final int NETWORK_ID = 75;

    public EntityCat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }
    private String ownerName = "";
    private boolean sitting = false;
    private boolean angry = false;
    private DyeColor collarColor = DyeColor.RED;//驯服后项圈为红色
    private IBehaviorGroup behaviorGroup;

    @Override
    public void updateMovement() {
        //猫猫流线运动怎么可能会摔落造成伤害呢~
        this.highestPosition = this.y;
        super.updateMovement();
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
    @Override
    public IBehaviorGroup getBehaviorGroup() {
        if (behaviorGroup == null) {
            behaviorGroup = new BehaviorGroup(
                    this.tickSpread,
                    Set.of(
                            //用于刷新InLove状态的核心行为
                            new Behavior(
                                    new InLoveExecutor(400),
                                    new AllMatchEvaluator(
                                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FED_TIME, 0, 400),
                                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                    ),
                                    1, 1
                            )
                    ),
                    Set.of(
                            new Behavior(new RandomRoamExecutor(0.25f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100), 4, 1),
                            new Behavior(new EntityBreedingExecutor<>(EntityCat.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 3, 1),
                            new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.25f, true), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 2, 1),
                            new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ConditionalProbabilityEvaluator(3, 7, entity -> entityHasOwner(entity, false, false), 10), 1, 1, 25),
                            new Behavior(new RandomRoamExecutor(0.1f, 12, 100, false, -1, true, 10), (entity -> true), 1, 1)
                    ),
                    Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }
    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.35f;
        }
        return 0.7f;
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
        if (this.namedTag.contains("Sitting")) {
            if (this.namedTag.getBoolean("Sitting")) {
                this.sitting = true;
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_SITTING, true);
            }
        }

        if (this.namedTag.contains("Angry")) {
            if (this.namedTag.getBoolean("Angry")) {
                this.angry = true;
                this.setDataFlag(DATA_FLAGS, DATA_FLAG_ANGRY, true);
            }
        }

        if (this.namedTag.contains("CollarColor")) {
            var collarColor = DyeColor.getByDyeData(this.namedTag.getByte("CollarColor"));
            if (collarColor == null) {
                this.collarColor = DyeColor.RED;
                this.setDataProperty(new ByteEntityData(DATA_COLOUR, DyeColor.RED.getWoolData()));
            } else {
                this.collarColor = collarColor;
                this.setDataProperty(new ByteEntityData(DATA_COLOUR, collarColor.getWoolData()));
            }
        }
    }

    /**
     * 绑定猫繁殖物品
     * WIKI了解只能使用生鲑鱼与生鳕鱼才能繁殖
     * <p>
     * Bound cat breeding items
     * WIKI understands that only raw salmon and raw cod can be used to breed
     * @param item 物品
     * @return
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemID.RAW_SALMON ||
                item.getId() == ItemID.ROTTEN_FLESH;
    }
    /**
     * 获得可以治疗猫的物品的治疗量
     * WIKI了解只有生鲑鱼与生鳕鱼才能恢复猫咪血量恢复2
     * <p>
     * Obtain healing amount of items that can heal cats
     * WIKI understands that only raw salmon and raw cod can restore the cat's blood recovery 2
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public int getHealableItem(Item item) {
        return switch (item.getId()) {
            case ItemID.RAW_FISH, ItemID.RAW_SALMON -> 2;
            default -> 0;
        };
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public boolean hasOwner(boolean checkOnline) {
        if (checkOnline) {
            if (this.ownerName == null || this.ownerName.isEmpty()) return false;
            var owner = getServer().getPlayerExact(this.ownerName);
            return owner != null;
        } else {
            return this.ownerName != null && !this.ownerName.isEmpty();
        }
    }
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    private boolean entityHasOwner(Entity entity, boolean checkOnline, boolean defaultValue) {
        if (entity instanceof EntityCat entityCat) {
            return entityCat.hasOwner(checkOnline);
        } else return defaultValue;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Cat";
    }
}
