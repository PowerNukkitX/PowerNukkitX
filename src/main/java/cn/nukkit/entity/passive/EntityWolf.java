package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCanAttack;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.*;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.RandomRoamExecutor;
import cn.nukkit.entity.ai.executor.entity.WolfAttackExecutor;
import cn.nukkit.entity.ai.executor.entity.WolfLookPlayerExecutor;
import cn.nukkit.entity.ai.executor.entity.WolfMoveToOwnerExecutor;
import cn.nukkit.entity.ai.memory.*;
import cn.nukkit.entity.ai.memory.entity.WolfNearestSkeletonMemory;
import cn.nukkit.entity.ai.route.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.ai.sensor.PlayerAttackEntitySensor;
import cn.nukkit.entity.ai.sensor.entity.WolfNearestFeedingPlayerSensor;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.LongEntityData;
import cn.nukkit.entity.mob.EntitySkeleton;
import cn.nukkit.entity.mob.EntityStray;
import cn.nukkit.entity.mob.EntityWitherSkeleton;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static cn.nukkit.entity.mob.EntityMob.DIFFICULTY_HAND_DAMAGE;

/**
 * @author BeYkeRYkt (Nukkit Project)
 * @author Cool_Loong (PowerNukkitX Project)
 * todo 野生狼不会被刷新
 */
public class EntityWolf extends EntityWalkingAnimal implements EntityTamable, EntityCanAttack {
    public static final int NETWORK_ID = 14;
    private Player owner = null;
    private String ownerName = "";
    private boolean sitting = false;
    private boolean angry = false;
    private DyeColor collarColor = DyeColor.RED;//项圈颜色
    private IBehaviorGroup behaviorGroup;
    private float[] diffHandDamage;

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
                                            new PassByTimeEvaluator<>(PlayerBreedingMemory.class, 0, 400),
                                            new PassByTimeEvaluator<>(InLoveMemory.class, 6000, Integer.MAX_VALUE, true)
                                    ),
                                    1, 1
                            )
                    ),
                    Set.of(
                            new Behavior(new EntityBreedingExecutor<>(EntityWolf.class, 16, 100, 0.35f), entity -> entity.getMemoryStorage().get(InLoveMemory.class).isInLove(), 6, 1),
                            //未驯服狼反击 todo 召集同伴
                            new Behavior(new WolfAttackExecutor(AttackTargetMemory.class, null, 0.35f, 33, true, 15), new AllMatchEvaluator(
                                    new MemoryCheckNotEmptyEvaluator(AttackTargetMemory.class),
                                    entity -> !entityHasOwner(entity, false, false)
                            ), 5, 1),
                            //驯服后的狼 攻击玩家击打的实体 和击打玩家的实体 以及附近的骷髅
                            new Behavior(new WolfAttackExecutor(PlayerAttackEntityMemory.class, WolfNearestSkeletonMemory.class, 0.35f, 33, true, 15), new AllMatchEvaluator(
                                    new AnyMatchEvaluator(
                                            new MemoryCheckNotEmptyEvaluator(PlayerAttackEntityMemory.class),
                                            new MemoryCheckNotEmptyEvaluator(WolfNearestSkeletonMemory.class)
                                    ),
                                    entity -> entityHasOwner(entity, true, false)
                            ), 5, 1),
                            //未驯服的狼攻击附近符合的实体
                            new Behavior(new WolfAttackExecutor(NearestEntityMemory.class, null, 0.35f, 14, true, 15), new AllMatchEvaluator(
                                    new MemoryCheckNotEmptyEvaluator(NearestEntityMemory.class),
                                    entity -> !entityHasOwner(entity, false, false),
                                    entity -> {
                                        var tmp = (Entity) entity.getMemoryData(NearestEntityMemory.class);
                                        if (tmp == null) return false;
                                        return attackTarget(tmp);
                                    }
                            ), 4, 1),
                            new Behavior(new WolfMoveToOwnerExecutor(0.35f, true, 15), entity -> {
                                if (entity instanceof EntityWolf entityWolf && entityWolf.hasOwner()) {
                                    var player = entityWolf.getServer().getPlayer(entityWolf.getOwnerName());
                                    if (player == null) return false;
                                    if (!player.isOnGround()) return false;
                                    if (this.isSitting()) return false;
                                    var distanceSquared = entity.distanceSquared(player);
                                    return distanceSquared >= 100;
                                } else return false;
                            }, 4, 1),
                            new Behavior(new WolfLookPlayerExecutor(), new MemoryCheckNotEmptyEvaluator(NearestFeedingPlayerMemory.class), 3, 1),
                            new Behavior(new LookAtTargetExecutor(NearestPlayerMemory.class, 150), new ConditionalProbabilityEvaluator(3, 7, entity -> entityHasOwner(entity, false, false), 10),
                                    1, 1, 25),
                            new Behavior(new RandomRoamExecutor(0.1f, 12, 150, false, -1, true, 10),
                                    new ProbabilityEvaluator(5, 10), 1, 1, 50)
                    ),
                    Set.of(new WolfNearestFeedingPlayerSensor(7, 0), new NearestPlayerSensor(8, 0, 20),
                            new NearestTargetEntitySensor<>(0, 20, 5, List.of(NearestEntityMemory.class, WolfNearestSkeletonMemory.class), this::attackTarget, entity -> switch (entity.getNetworkId()) {
                                case EntitySkeleton.NETWORK_ID, EntityWitherSkeleton.NETWORK_ID, EntityStray.NETWORK_ID ->
                                        true;
                                default -> false;
                            }), new PlayerAttackEntitySensor(5, false)
                    ),
                    Set.of(new WalkController(), new LookController(true, true)),
                    new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)
            );
        }
        return behaviorGroup;
    }

    public EntityWolf(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.3f;
        }
        return 0.6f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.425f;
        }
        return 0.8f;
    }

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Wolf";
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setMaxHealth(8);
        this.setHealth(8);

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

        if (this.namedTag.contains(DIFFICULTY_HAND_DAMAGE)) {
            var damageList = this.namedTag.getList(DIFFICULTY_HAND_DAMAGE, FloatTag.class);
            this.diffHandDamage[0] = damageList.get(0).getData();
            this.diffHandDamage[1] = damageList.get(1).getData();
            this.diffHandDamage[2] = damageList.get(2).getData();
        }

        if (this.diffHandDamage == null) {
            this.setDiffHandDamage(new float[]{3, 4, 6});
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putBoolean("Angry", this.angry);
        this.namedTag.putByte("CollarColor", this.collarColor.getDyeData());
        this.namedTag.putBoolean("Sitting", this.sitting);
        this.namedTag.putString("OwnerName", this.ownerName);
        if (diffHandDamage != null)
            this.namedTag.putList(new ListTag<FloatTag>(DIFFICULTY_HAND_DAMAGE).add(new FloatTag("", this.diffHandDamage[0])).add(new FloatTag("", this.diffHandDamage[1])).add(new FloatTag("", this.diffHandDamage[2])));
    }

    @Override
    public boolean onUpdate(int currentTick) {
        var result = super.onUpdate(currentTick);

        //initEntity的时候玩家还没连接，所以只能在onUpdate里面更新
        if (this.namedTag.contains("OwnerName") && this.owner == null) {
            String ownerName = namedTag.getString("OwnerName");
            if (ownerName != null && ownerName.length() > 0) {
                setOwnerName(ownerName);
            }
        }
        return result;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            return applyNameTag(player, item);
        }

        int healable = this.getHealableItem(item);
        if (item.getId() == ItemID.BONE) {
            if (!this.hasOwner() && !this.isAngry()) {
                player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                if (Utils.rand(1, 3) == 3) {
                    EntityEventPacket packet = new EntityEventPacket();
                    packet.eid = this.getId();
                    packet.event = EntityEventPacket.TAME_SUCCESS;
                    player.dataPacket(packet);

                    this.setMaxHealth(20);
                    this.setHealth(20);
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
            this.getLevel().addSound(this, Sound.RANDOM_EAT);
            this.getLevel().addParticle(new ItemBreakParticle(this.add(0, getHeight() * 0.75F, 0), Item.get(item.getId(), 0, 1)));

            if (healable != 0) {
                this.setHealth(Math.max(this.getMaxHealth(), this.getHealth() + healable));
            }

            getMemoryStorage().get(PlayerBreedingMemory.class).setData(player);
            return true;
        } else if (this.hasOwner() && player.getName().equals(getOwnerName())) {
            this.setSitting(!this.isSitting());
            return true;
        }

        return false;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        var result = super.attack(source);
        if (source instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            if (entityDamageByEntityEvent.getDamager() instanceof Player player && player.isCreative()) return result;
            //更新仇恨目标
            getMemoryStorage().setData(AttackTargetMemory.class, entityDamageByEntityEvent.getDamager());
        }
        return result;
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
    public String getOwnerName() {
        return this.ownerName;
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
    public void setOwnerName(String playerName) {
        var player = getServer().getPlayerExact(playerName);
        if (player == null) return;
        this.owner = player;
        this.ownerName = playerName;
        this.setDataProperty(new LongEntityData(DATA_OWNER_EID, player.getId()));
        this.setTamed(true);
        this.namedTag.putString("OwnerName", this.ownerName);
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Nullable
    @Override
    public Player getOwner() {
        return this.owner;
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    @Override
    public boolean hasOwner() {
        return hasOwner(true);
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
    public boolean isSitting() {
        return this.sitting;
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public void setSitting(boolean sit) {
        this.sitting = sit;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_SITTING, sit);
        this.namedTag.putBoolean("Sitting", sit);
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    private void setTamed(boolean tamed) {
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_TAMED, tamed);
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public boolean isAngry() {
        return this.angry;
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public void setAngry(boolean angry) {
        this.angry = angry;
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_ANGRY, angry);
        this.namedTag.putBoolean("Angry", angry);
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
    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemID.RAW_CHICKEN ||
                item.getId() == ItemID.COOKED_CHICKEN ||
                item.getId() == ItemID.RAW_BEEF ||
                item.getId() == ItemID.COOKED_BEEF ||
                item.getId() == ItemID.RAW_MUTTON ||
                item.getId() == ItemID.COOKED_MUTTON ||
                item.getId() == ItemID.RAW_PORKCHOP ||
                item.getId() == ItemID.COOKED_PORKCHOP ||
                item.getId() == ItemID.RAW_RABBIT ||
                item.getId() == ItemID.COOKED_RABBIT ||
                item.getId() == ItemID.ROTTEN_FLESH;
    }

    /**
     * 获得可以治疗狼的物品的治疗量
     */
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public int getHealableItem(Item item) {
        return switch (item.getId()) {
            case ItemID.RAW_PORKCHOP, ItemID.RAW_BEEF, ItemID.RAW_RABBIT -> 3;
            case ItemID.COOKED_PORKCHOP, ItemID.COOKED_BEEF -> 8;
            case ItemID.RAW_FISH, ItemID.RAW_SALMON, ItemID.RAW_CHICKEN, ItemID.RAW_MUTTON -> 2;
            case ItemID.CLOWNFISH, ItemID.PUFFERFISH -> 1;
            case ItemID.COOKED_FISH, ItemID.COOKED_RABBIT -> 5;
            case ItemID.COOKED_SALMON, ItemID.COOKED_CHICKEN, ItemID.COOKED_MUTTON -> 6;
            case ItemID.ROTTEN_FLESH -> 4;
            case ItemID.RABBIT_STEW -> 10;
            default -> 0;
        };
    }

    //兔子、狐狸、骷髅及其变种、羊驼、绵羊和小海龟。然而它们被羊驼啐唾沫时会逃跑。
    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    public boolean attackTarget(Entity entity) {
        return switch (entity.getNetworkId()) {
            case EntityRabbit.NETWORK_ID, EntityFox.NETWORK_ID, EntitySkeleton.NETWORK_ID, EntityWitherSkeleton.NETWORK_ID, EntityStray.NETWORK_ID, EntityLlama.NETWORK_ID,
                    EntitySheep.NETWORK_ID, EntityTurtle.NETWORK_ID -> true;
            default -> false;
        };
    }

    @PowerNukkitXOnly
    @Since("1.19.30-r1")
    private boolean entityHasOwner(Entity entity, boolean checkOnline, boolean defaultValue) {
        if (entity instanceof EntityWolf entityWolf) {
            return entityWolf.hasOwner(checkOnline);
        } else return defaultValue;
    }

    @Override
    public float[] getDiffHandDamage() {
        return this.diffHandDamage;
    }

    @Override
    public float getDiffHandDamage(int difficulty) {
        return diffHandDamage[difficulty - 1];
    }

    @Override
    public void setDiffHandDamage(float[] damages) {
        this.diffHandDamage = damages;
        this.namedTag.putList(new ListTag<FloatTag>(DIFFICULTY_HAND_DAMAGE).add(new FloatTag("", this.diffHandDamage[0])).add(new FloatTag("", this.diffHandDamage[1])).add(new FloatTag("", this.diffHandDamage[2])));
    }

    @Override
    public void setDiffHandDamage(int difficulty, float damage) {
        this.diffHandDamage[difficulty - 1] = damage;
        this.namedTag.putList(new ListTag<FloatTag>(DIFFICULTY_HAND_DAMAGE).add(new FloatTag("", this.diffHandDamage[0])).add(new FloatTag("", this.diffHandDamage[1])).add(new FloatTag("", this.diffHandDamage[2])));
    }
}
