package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanSit;
import cn.nukkit.entity.EntityTamable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.ConditionalProbabilityEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.*;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.ai.sensor.WolfNearestFeedingPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EntityCat extends EntityWalkingAnimal implements EntityTamable, EntityCanSit {
    public static final int NETWORK_ID = 75;
    private DyeColor collarColor = DyeColor.RED;//驯服后项圈为红色
    private IBehaviorGroup behaviorGroup;
    //猫咪有11种颜色变种
    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private int variant;

    public EntityCat(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void updateMovement() {
        //猫猫流线运动怎么可能会摔落造成伤害呢~
        this.highestPosition = this.y;
        super.updateMovement();
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
                                    all(
                                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FED_TIME, 0, 400),
                                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE),
                                            (entity) -> this.hasOwner()
                                    ),
                                    1, 1
                            ),
                            //流浪猫在以自身为半径15格鸡,兔子,小海龟 来自Wiki https://minecraft.fandom.com/wiki/Cat#Bedrock_Edition
                            new Behavior(
                                    entity -> {
                                        var storage = getMemoryStorage();
                                        Entity attackTarget = null;
                                        //已驯服为家猫就不攻击下述动物反之未驯服为流浪猫攻击下述动物
                                        //攻击最近的鸡，小海龟，兔子
                                        if (storage.notEmpty(CoreMemoryTypes.CAT_NEAREST_SUITABLE_ATTACK_TARGET) && storage.get(CoreMemoryTypes.CAT_NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                            attackTarget = storage.get(CoreMemoryTypes.CAT_NEAREST_SUITABLE_ATTACK_TARGET);
                                        }
                                        storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                        return true;
                                    },
                                    entity -> true, 1
                            )
                    ),
                    Set.of(
                            //坐下锁定 优先级7
                            new Behavior(entity -> false, entity -> this.isSitting(), 7),
                            //猫咪繁殖 优先级5
                            new Behavior(new EntityBreedingExecutor<>(EntityCat.class, 8, 100, 0.45f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 5, 1),
                            //猫咪向主人移动 优先级4
                            new Behavior(new EntityMoveToOwnerExecutor(0.35f, true, 15), entity -> {
                                if (entity instanceof EntityCat entityCat && entityCat.hasOwner()) {
                                    var player = getOwner();
                                    if (player == null) return false;
                                    if (!player.isOnGround()) return false;
                                    var distanceSquared = entity.distanceSquared(player);
                                    return distanceSquared >= 100;
                                } else return false;
                            }, 4, 1),
                            //猫咪看向食物 优先级3
                            new Behavior(new LookAtFeedingPlayerExecutor(), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 3, 1),
                            //猫咪随机目标点移动 优先级1
                            new Behavior(new RandomRoamExecutor(0.2f, 12, 150, false, -1, true, 20), new ProbabilityEvaluator(5, 10), 1, 1, 50),
                            //猫咪看向目标玩家 优先级1
                            new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 200), new ConditionalProbabilityEvaluator(3, 7, entity -> entityHasOwner(entity, false, false), 10), 1, 1, 25)
                    ),
                    Set.of(new WolfNearestFeedingPlayerSensor(7, 0),
                            new NearestPlayerSensor(8, 0, 20),
                            new NearestTargetEntitySensor<>(0, 15, 20,
                                    List.of(CoreMemoryTypes.CAT_NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
                    ),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)

            );
        }
        return behaviorGroup;
    }

    //猫咪身体大小来自Wiki https://minecraft.fandom.com/wiki/Cat
    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.24f;
        }
        return 0.48f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.28f;
        }
        return 0.56f;
    }

    //攻击选择器
    //流浪猫会攻击兔子,鸡,小海龟
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public boolean attackTarget(Entity entity) {
        return switch (entity.getNetworkId()) {
            case EntityRabbit.NETWORK_ID, EntityChicken.NETWORK_ID, EntityTurtle.NETWORK_ID -> true;
            default -> false;
        };
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();

        if (this.namedTag.contains("CollarColor")) {
            var collarColor = DyeColor.getByDyeData(this.namedTag.getByte("CollarColor"));
            if (collarColor == null) {
                this.collarColor = DyeColor.RED;
                this.setDataProperty(new ByteEntityData(DATA_COLOUR, DyeColor.RED.getDyeData()));
            } else {
                this.collarColor = collarColor;
                this.setDataProperty(new ByteEntityData(DATA_COLOUR, collarColor.getWoolData()));
            }
        } else this.collarColor = DyeColor.RED;
        if (this.namedTag.contains("Variant")) {
            this.variant = this.namedTag.getInt("Variant");
        } else {
            this.variant = getRandomVariant();
        }
        this.setDataProperty(new IntEntityData(DATA_VARIANT, this.variant));
    }

    private int getRandomVariant() {
        return VARIANTS[Utils.rand(0, VARIANTS.length - 1)];
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("CollarColor", this.collarColor.getDyeData());
        this.namedTag.putInt("Variant", this.variant);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            return applyNameTag(player, item);
        }
        int healable = this.getHealableItem(item);
        if (item.getId() == ItemID.RAW_FISH && item.getId() == ItemID.RAW_SALMON) {
            if (!this.hasOwner()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                if (Utils.rand(1, 3) == 3) {
                    EntityEventPacket packet = new EntityEventPacket();
                    packet.eid = this.getId();
                    packet.event = EntityEventPacket.TAME_SUCCESS;
                    player.dataPacket(packet);

                    this.setMaxHealth(10);
                    this.setHealth(10);
                    this.setOwnerName(player.getName());
                    this.setCollarColor(DyeColor.RED);
                    this.saveNBT();

                    this.getLevel().dropExpOrb(this, Utils.rand(1, 7));

                    return true;
                } else {
                    EntityEventPacket packet = new EntityEventPacket();
                    packet.eid = this.getId();
                    packet.event = EntityEventPacket.TAME_FAIL;
                    player.dataPacket(packet);
                }
            }
        } else if (item.getId() == Item.DYE) {
            if (this.hasOwner() && player.equals(this.getOwner())) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                this.setCollarColor(((ItemDye) item).getDyeColor());
                return true;
            }
        } else if (this.isBreedingItem(item)) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            this.getLevel().addSound(this, Sound.MOB_CAT_EAT);
            this.getLevel().addParticle(new ItemBreakParticle(this.add(0, getHeight() * 0.75F, 0), Item.get(item.getId(), 0, 1)));

            if (healable != 0) {
                this.setHealth(Math.max(this.getMaxHealth(), this.getHealth() + healable));
            }
            getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FED_TIME, Server.getInstance().getTick());
            getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
            return true;
        } else if (this.hasOwner() && player.getName().equals(getOwnerName())) {
            this.setSitting(!this.isSitting());
            return true;
        }

        return false;
    }

    //击杀猫会掉落0-2根线
    //击杀小猫不会获得
    @Override
    public Item[] getDrops() {
        if (!this.isBaby()) {
            int catdrops = Utils.rand(0, 2);
            if (catdrops > 0)
                return new Item[]{Item.get(Item.STRING, 0, catdrops)};
        }
        return Item.EMPTY_ARRAY;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Cat";
    }

    /**
     * 绑定猫繁殖物品
     * WIKI了解只能使用生鲑鱼与生鳕鱼才能繁殖
     * <p>
     * Bound cat breeding items
     * WIKI understands that only raw salmon and raw cod can be used to breed
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemID.RAW_SALMON ||
                item.getId() == ItemID.RAW_FISH;
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
    private void setTamed(boolean tamed) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_TAMED, tamed);
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public void setCollarColor(DyeColor color) {
        this.collarColor = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color.getWoolData()));
        this.namedTag.putByte("CollarColor", color.getDyeData());
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    private boolean entityHasOwner(Entity entity, boolean checkOnline, boolean defaultValue) {
        if (entity instanceof EntityCat entityCat) {
            return entityCat.hasOwner(checkOnline);
        } else return defaultValue;
    }
}