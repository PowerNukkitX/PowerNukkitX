package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.*;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
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
import cn.nukkit.entity.ai.sensor.EntityAttackedByOwnerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.entity.ai.sensor.WolfNearestFeedingPlayerSensor;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.ItemBreakParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 * @author Cool_Loong (PowerNukkitX Project)
 * todo 野生狼不会被刷新
 */
public class EntityWolf extends EntityAnimal implements EntityWalkable, EntityOwnable, EntityCanAttack, EntityCanSit, EntityAngryable, EntityHealable, EntityColor {
    @Override
    @NotNull
    public String getIdentifier() {
        return WOLF;
    }

    protected float[] diffHandDamage = new float[]{3, 4, 6};

    public EntityWolf(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }


    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //用于刷新InLove状态的核心行为
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE),
                                        //只有拥有主人的狼才能交配
                                        //Only wolves with a master can mate
                                        (entity) -> this.hasOwner()
                                ),
                                1, 1
                        ),
                        //刷新攻击目标
                        new Behavior(
                                entity -> {
                                    var storage = getMemoryStorage();
                                    var hasOwner = hasOwner();
                                    Entity attackTarget = null;
                                    var attackEvent = storage.get(CoreMemoryTypes.BE_ATTACKED_EVENT);
                                    EntityDamageByEntityEvent attackByEntityEvent = null;
                                    if (attackEvent instanceof EntityDamageByEntityEvent attackByEntityEv)
                                        attackByEntityEvent = attackByEntityEv;
                                    boolean validAttacker = attackByEntityEvent != null && attackByEntityEvent.getDamager().isAlive() && (!(attackByEntityEvent.getDamager() instanceof Player player) || player.isSurvival());
                                    if (hasOwner) {
                                        //已驯服
                                        if (storage.notEmpty(CoreMemoryTypes.ENTITY_ATTACKING_OWNER) && storage.get(CoreMemoryTypes.ENTITY_ATTACKING_OWNER).isAlive() && !storage.get(CoreMemoryTypes.ENTITY_ATTACKING_OWNER).equals(this)) {
                                            //攻击攻击主人的生物(排除自己)
                                            attackTarget = storage.get(CoreMemoryTypes.ENTITY_ATTACKING_OWNER);
                                            storage.clear(CoreMemoryTypes.ENTITY_ATTACKING_OWNER);
                                        } else if (storage.notEmpty(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER) && storage.get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER).isAlive() && !storage.get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER).equals(this)) {
                                            //攻击主人攻击的生物
                                            attackTarget = storage.get(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
                                            storage.clear(CoreMemoryTypes.ENTITY_ATTACKED_BY_OWNER);
                                        } else if (attackByEntityEvent != null && validAttacker && !attackByEntityEvent.getDamager().equals(getOwner())) {
                                            //攻击攻击自己的生物（主人例外）
                                            attackTarget = attackByEntityEvent.getDamager();
                                            storage.clear(CoreMemoryTypes.BE_ATTACKED_EVENT);
                                        } else if (storage.notEmpty(CoreMemoryTypes.NEAREST_SKELETON) && storage.get(CoreMemoryTypes.NEAREST_SKELETON).isAlive()) {
                                            //攻击最近的骷髅
                                            attackTarget = storage.get(CoreMemoryTypes.NEAREST_SKELETON);
                                            storage.clear(CoreMemoryTypes.NEAREST_SKELETON);
                                        }
                                    } else {
                                        //未驯服
                                        if (validAttacker) {
                                            //攻击攻击自己的生物
                                            attackTarget = attackByEntityEvent.getDamager();
                                            storage.clear(CoreMemoryTypes.BE_ATTACKED_EVENT);
                                        } else if (storage.notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET) && storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                            //攻击最近的合适生物
                                            attackTarget = storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                            storage.clear(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                        }
                                    }
                                    storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                    return false;
                                },
                                entity -> this.getMemoryStorage().isEmpty(CoreMemoryTypes.ATTACK_TARGET), 1
                        )
                ),
                Set.of(
                        //坐下锁定
                        new Behavior(entity -> false, entity -> this.isSitting(), 7),
                        //攻击仇恨目标 todo 召集同伴
                        new Behavior(new WolfAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.7f, 33, true, 20),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET)
                                , 6, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntityWolf.class, 16, 100, 0.35f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 5, 1),
                        new Behavior(new EntityMoveToOwnerExecutor(0.7f, true, 15), entity -> {
                            if (this.hasOwner()) {
                                var player = getOwner();
                                if (!player.isOnGround()) return false;
                                var distanceSquared = this.distanceSquared(player);
                                return distanceSquared >= 100;
                            } else return false;
                        }, 4, 1),
                        new Behavior(new LookAtFeedingPlayerExecutor(), new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER), 3, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ConditionalProbabilityEvaluator(3, 7, entity -> hasOwner(false), 10),
                                1, 1, 25),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 150, false, -1, true, 10),
                                new ProbabilityEvaluator(5, 10), 1, 1, 50)
                ),
                Set.of(new WolfNearestFeedingPlayerSensor(7, 0),
                        new NearestPlayerSensor(8, 0, 20),
                        new NearestTargetEntitySensor<>(0, 20, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, CoreMemoryTypes.NEAREST_SKELETON), this::attackTarget,
                                entity -> switch (entity.getIdentifier().toString()) {
                                    case SKELETON, WITHER_SKELETON, STRAY -> true;
                                    default -> false;
                                }),
                        new EntityAttackedByOwnerSensor(5, false)
                ),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
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

    @Override
    public String getOriginalName() {
        return "Wolf";
    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
        super.initEntity();
        //update CollarColor to Color
        if (namedTag.contains("CollarColor")) {
            this.setColor(DyeColor.getByWoolData(namedTag.getByte("CollarColor")));
        }
    }

    @Override
    public boolean onUpdate(int currentTick) {
        //同步owner eid
        if (hasOwner()) {
            Player owner = getOwner();
            if (owner != null && getDataProperty(Entity.OWNER_EID) != owner.getId()) {
                this.setDataProperty(Entity.OWNER_EID, owner.getId());
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.NAME_TAG && !player.isAdventure()) {
            return applyNameTag(player, item);
        }

        int healable = this.getHealingAmount(item);
        //对于狼，只有骨头才能驯服，故此需要特判
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
                    this.setColor(DyeColor.RED);
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
                this.setColor(((ItemDye) item).getDyeColor());
                return true;
            }
        } else if (this.isBreedingItem(item)) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            this.getLevel().addSound(this, Sound.RANDOM_EAT);
            this.getLevel().addParticle(new ItemBreakParticle(this.add(0, getHeight() * 0.75F, 0), Item.get(item.getId(), 0, 1)));

            if (healable != 0) {
                this.setHealth(Math.max(this.getMaxHealth(), this.getHealth() + healable));
            }

            getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
            getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, Server.getInstance().getTick());
            return true;
        } else if (this.hasOwner() && player.getName().equals(getOwnerName()) && !this.isTouchingWater()) {
            this.setSitting(!this.isSitting());
            return false;
        }

        return false;
    }

    @Override
    public boolean isBreedingItem(Item item) {
        return item.getId() == ItemID.CHICKEN ||
                item.getId() == ItemID.COOKED_CHICKEN ||
                item.getId() == ItemID.BEEF ||
                item.getId() == ItemID.COOKED_BEEF ||
                item.getId() == ItemID.MUTTON ||
                item.getId() == ItemID.COOKED_MUTTON ||
                item.getId() == ItemID.PORKCHOP ||
                item.getId() == ItemID.COOKED_PORKCHOP ||
                item.getId() == ItemID.RABBIT ||
                item.getId() == ItemID.COOKED_RABBIT ||
                item.getId() == ItemID.ROTTEN_FLESH;
    }

    /**
     * 获得可以治疗狼的物品的治疗量
     */
    public int getHealingAmount(Item item) {
        return switch (item.getId()) {
            case ItemID.PORKCHOP, ItemID.BEEF, ItemID.RABBIT -> 3;
            case ItemID.COOKED_PORKCHOP, ItemID.COOKED_BEEF -> 8;
            case ItemID.COD, ItemID.SALMON, ItemID.CHICKEN, ItemID.MUTTON -> 2;
            case ItemID.TROPICAL_FISH, ItemID.PUFFERFISH -> 1;
            case ItemID.COOKED_COD, ItemID.COOKED_RABBIT -> 5;
            case ItemID.COOKED_SALMON, ItemID.COOKED_CHICKEN, ItemID.COOKED_MUTTON -> 6;
            case ItemID.ROTTEN_FLESH -> 4;
            case ItemID.RABBIT_STEW -> 10;
            default -> 0;
        };
    }

    //兔子、狐狸、骷髅及其变种、羊驼、绵羊和小海龟。然而它们被羊驼啐唾沫时会逃跑。


    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case RABBIT, FOX, SKELETON, WITHER_SKELETON, STRAY, LLAMA, SHEEP, TURTLE -> true;
            default -> false;
        };
    }

    @Override
    public float[] getDiffHandDamage() {
        return diffHandDamage;
    }
}
