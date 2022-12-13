package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.*;
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
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
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

import java.util.List;
import java.util.Set;

public class EntityCat extends EntityWalkingAnimal implements EntityTamable, EntityCanSit, EntityCanAttack, EntityHealable {
    public static final int NETWORK_ID = 75;
    private DyeColor collarColor = DyeColor.RED;//驯服后项圈为红色
    private IBehaviorGroup behaviorGroup;
    //猫咪有11种颜色变种
    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private int variant;
    protected float[] diffHandDamage = new float[]{4, 4, 4};

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
                            new Behavior((entity) -> {
                                //刷新随机播放音效
                                //当猫被驯服时发出的声音
                                if (this.hasOwner())
                                    this.setAmbientSoundEvent(Sound.MOB_CAT_MEOW);
                                    //当猫在爱爱时发出的声音
                                else if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.IS_IN_LOVE))
                                    this.setAmbientSoundEvent(Sound.MOB_CAT_PURR);
                                    //当猫爱爱结束时发出的声音
                                else if (this.getMemoryStorage().notEmpty(CoreMemoryTypes.LAST_IN_LOVE_TIME))
                                    this.setAmbientSoundEvent(Sound.MOB_CAT_PURREOW);
                                else
                                    this.setAmbientSoundEvent(Sound.MOB_CAT_STRAYMEOW);

                                return false;
                            }, (entity) -> true, 1, 1, 20),
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
                            //"流浪猫会寻找并攻击15格内的鸡[仅Java版]、兔子和幼年海龟" --- 来自Wiki https://minecraft.fandom.com/wiki/Cat#Bedrock_Edition
                            new Behavior(
                                    entity -> {
                                        if (this.hasOwner(false)) return false;
                                        var storage = getMemoryStorage();
                                        if (storage.notEmpty(CoreMemoryTypes.ATTACK_TARGET)) return false;
                                        Entity attackTarget = null;
                                        //已驯服为家猫就不攻击下述动物反之未驯服为流浪猫攻击下述动物
                                        //攻击最近的鸡，小海龟，兔子
                                        if (storage.notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) && storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                            attackTarget = storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                        }
                                        storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                        return false;
                                    },
                                    entity -> true, 20
                            )
                    ),
                    Set.of(
                            //坐下锁定 优先级8
                            new Behavior(entity -> false, entity -> this.isSitting(), 8),
                            //睡觉 优先级7
                            new Behavior(new SleepOnOwnerBedExecutor(), entity -> {
                                var player = this.getOwner();
                                if (player == null) return false;
                                if (player.getLevel().getId() != this.level.getId()) return false;
                                return player.isSleeping();
                            }, 7),
                            //攻击仇恨目标 优先级6
                            new Behavior(new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.35f, 15, true, 10), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET), 6),
                            //猫咪繁殖 优先级5
                            new Behavior(new EntityBreedingExecutor<>(EntityCat.class, 8, 100, 0.35f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 5),
                            //猫咪向主人移动 优先级4
                            new Behavior(new EntityMoveToOwnerExecutor(0.35f, true, 15), entity -> {
                                if (this.hasOwner()) {
                                    var player = getOwner();
                                    if (!player.isOnGround()) return false;
                                    var distanceSquared = entity.distanceSquared(player);
                                    return distanceSquared >= 100;
                                } else return false;
                            }, 4),
                            //猫在主人身边随机移动 优先级3
                            new Behavior(new FlatRandomRoamExecutor(0.1f, 4, 100, false, -1, true, 20), entity -> this.hasOwner() && this.getOwner().distanceSquared(this) < 100, 3),
                            //猫咪看向食物 优先级3
                            new Behavior(new LookAtFeedingPlayerExecutor(), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 3),
                            //猫咪随机目标点移动 优先级1
                            new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 150, false, -1, true, 20), new ProbabilityEvaluator(5, 10), 1, 1, 50),
                            //猫咪看向目标玩家 优先级1
                            new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ConditionalProbabilityEvaluator(3, 7, entity -> hasOwner(false), 10), 1, 1, 25)
                    ),
                    Set.of(new NearestFeedingPlayerSensor(7, 0),
                            new NearestPlayerSensor(8, 0, 20),
                            new NearestTargetEntitySensor<>(0, 15, 20,
                                    List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
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
        return this.isBaby() ? 0.24f : 0.48f;
    }

    @Override
    public float getHeight() {
        return this.isBaby() ? 0.28f : 0.56f;
    }

    //攻击选择器
    //流浪猫会攻击兔子,鸡,小海龟
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
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

        int healable = this.getHealingAmount(item);

        if (this.isBreedingItem(item)) {
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
            } else {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                this.getLevel().addSound(this, Sound.MOB_CAT_EAT);
                this.getLevel().addParticle(new ItemBreakParticle(this.add(0, getHeight() * 0.75F, 0), Item.get(item.getId(), 0, 1)));

                if (healable != 0) {
                    this.setHealth(Math.max(this.getMaxHealth(), this.getHealth() + healable));
                }
                getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FED_TIME, Server.getInstance().getTick());
                getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
                return true;
            }
        } else if (item.getId() == Item.DYE) {
            if (this.hasOwner() && player.equals(this.getOwner())) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                this.setCollarColor(((ItemDye) item).getDyeColor());
                return true;
            }
        } else if (this.hasOwner() && player.getName().equals(getOwnerName())) {
            this.setSitting(!this.isSitting());
            return false;
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
    public int getHealingAmount(Item item) {
        return switch (item.getId()) {
            case ItemID.RAW_FISH, ItemID.RAW_SALMON -> 2;
            default -> 0;
        };
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public void setCollarColor(DyeColor color) {
        this.collarColor = color;
        this.setDataProperty(new ByteEntityData(DATA_COLOUR, color.getWoolData()));
        this.namedTag.putByte("CollarColor", color.getDyeData());
    }

    @Override
    public float[] getDiffHandDamage() {
        return diffHandDamage;
    }
}