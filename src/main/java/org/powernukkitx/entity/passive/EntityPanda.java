package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCanAttack;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.*;
import org.powernukkitx.entity.ai.executor.panda.EatingExecutor;
import org.powernukkitx.entity.ai.executor.panda.LayingExecutor;
import org.powernukkitx.entity.ai.executor.panda.RollExecutor;
import org.powernukkitx.entity.ai.executor.panda.ShakeExecutor;
import org.powernukkitx.entity.ai.executor.panda.SittingExecutor;
import org.powernukkitx.entity.ai.executor.panda.SneezingExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestItemSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.item.EntityItem;
import org.powernukkitx.entity.mob.EntityEnderDragon;
import org.powernukkitx.entity.mob.EntityGhast;
import org.powernukkitx.entity.mob.EntityMagmaCube;
import org.powernukkitx.entity.mob.EntityMob;
import org.powernukkitx.entity.mob.EntityPhantom;
import org.powernukkitx.entity.mob.EntityShulker;
import org.powernukkitx.entity.mob.EntitySlime;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.inventory.EntityEquipmentInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.TakeItemActorPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EntityPanda extends EntityAnimal implements EntityWalkable, EntityCanAttack, EntityVariant, InventoryHolder {
    @Override
    @NotNull
    public String getIdentifier() {
        return PANDA;
    }

    @Getter
    protected float[] diffHandDamage;
    private final static int[] VARIANTS = new int[]{0, 1, 2, 3, 4, 5, 6};

    public static final int NORMAL = 0;
    public static final int LAZY = 1;
    public static final int WORRIED = 2;
    public static final int PLAYFUL = 3;
    public static final int BROWN = 4;
    public static final int WEAK = 5;
    public static final int AGRESSIVE = 6;

    private EntityEquipmentInventory inventory = new EntityEquipmentInventory(this);

    public EntityPanda(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public int getVariant() {
        return isInitialized() ? getMemoryStorage().get(CoreMemoryTypes.VARIANT) : 0;
    }

    @Override
    public float getWidth() {
        return 1.7f;
    }

    @Override
    public float getHeight() {
        return 1.5f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        int variantHealth = getVariant() == WEAK ? 10 : 20;
        return HealthComponent.value(variantHealth);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        float varMovement = getVariant() == LAZY ? 0.07f : 0.15f;
        return MovementComponent.value(varMovement);
    }

    @Override
    public String getOriginalName() {
        return "Panda";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("panda", "panda_aggressive", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(BlockID.BAMBOO),
                List.of(new BreedableComponent.BreedsWith(EntityID.PANDA, EntityID.PANDA)),
                null,
                null,
                List.of(
                        new BreedableComponent.EnvironmentRequirement(
                                Set.of(BlockID.BAMBOO),
                                8,
                                5.0f
                        )
                ),
                null,
                null,
                new BreedableComponent.MutationFactor(1.0f, null, null),
                null,
                null,
                false,
                null
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                        new AgeableComponent.FeedItem(BlockID.BAMBOO)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return false;
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int randomVariant() {
        int random = Utils.rand(0, 16);
        if (random < 5) return NORMAL;
        if (random < 6) return LAZY;
        if (random < 7) return WORRIED;
        if (random < 8) return PLAYFUL;
        if (random < 10) return BROWN;
        if (random < 15) return WEAK;
        if (random < 16) return AGRESSIVE;
        return 0;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        return new Item[]{
                Item.get(Block.BAMBOO, 0, Utils.rand(0, 3))
        };
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (currentTick % 20 == 0) {
            for (Entity entity : this.level.getNearbyEntities(this.boundingBox, this)) {
                if (entity == null) continue;
                if (!entity.isAlive() || !this.isAlive()) {
                    continue;
                }
                if (entity instanceof EntityItem entityItem) {
                    Item item = entityItem.getItem();
                    if (item.getId().equals(Block.BAMBOO)) {
                        if (!getInventory().callPickupItemEvent(entityItem)) {
                            continue;
                        }
                        getInventory().addItem(item);
                        final TakeItemActorPacket pk = new TakeItemActorPacket();
                        pk.setActorRuntimeID(this.getId());
                        pk.setItemRuntimeID(entity.getId());
                        Server.broadcastPacket(getViewers().values(), pk);
                        entityItem.close();
                    }
                }
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public void initEntity() {
        this.diffHandDamage = new float[]{4f, 6f, 9f};
        super.initEntity();
        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }
        getMemoryStorage().put(CoreMemoryTypes.LAST_SITTING_CHECK, getLevel().getTick());
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (super.attack(source)) {
            if (source instanceof EntityDamageByEntityEvent event) {
                if (event.getDamager() instanceof Player player) {
                    Arrays.stream(getLevel().getCollidingEntities(getBoundingBox().grow(10, 5, 10))).filter(entity -> entity instanceof EntityPanda panda && panda.getVariant() == AGRESSIVE).map(entity -> (EntityPanda) entity).forEach(entity -> {
                                entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, player);
                                entity.setDataFlag(ActorFlags.ANGRY, true);
                            }
                    );
                }
            }
            return true;
        } else return false;
    }

    private class PandaAttackEecutor extends MeleeAttackExecutor {
        public PandaAttackEecutor() {
            super(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 16, true, 20);
        }

        @Override
        protected void playAttackAnimation(EntityIntelligent entity) {
            super.playAttackAnimation(entity);
            if (entity instanceof EntityVariant variant) {
                if (variant.getVariant() != AGRESSIVE) {
                    stop(entity);
                }
                entity.getLevel().addSound(entity, Sound.MOB_PANDA_BITE);
            }
        }

        public void stop(EntityIntelligent entity) {
            entity.getMemoryStorage().clear(CoreMemoryTypes.ATTACK_TARGET);
            entity.setDataFlag(ActorFlags.ANGRY, false);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            stop(entity);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            stop(entity);
        }
    }

    public class PandaSittingEvaluator implements IBehaviorEvaluator {

        private final int cooldown;

        public PandaSittingEvaluator(int cooldown) {
            this.cooldown = cooldown * 20;
        }

        @Override
        public boolean evaluate(EntityIntelligent entity) {
            boolean evaluate = any(
                    all(
                            entity1 -> {
                                if (getLevel().getTick() - getMemoryStorage().get(CoreMemoryTypes.LAST_SITTING_CHECK) > cooldown) {
                                    getMemoryStorage().put(CoreMemoryTypes.LAST_SITTING_CHECK, getLevel().getTick());
                                    return true;
                                } else return false;
                            },
                            new ProbabilityEvaluator(3, 10)
                    ),
                    entity1 -> getDataFlag(ActorFlags.SITTING)
            ).evaluate(entity);
            return evaluate;
        }
    }

    private static class PandaNearestItemSensor extends NearestItemSensor {
        public PandaNearestItemSensor(double range, double minRange) {
            super(range, minRange);
        }

        @Override
        public void sense(EntityIntelligent entity) {

            EntityItem item = null;
            double rangeSquared = this.range * this.range;
            double minRangeSquared = this.minRange * this.minRange;
            // Find the nearest player within range
            for (Entity e : entity.getLevel().getEntities()) {
                if (e instanceof EntityItem entityItem) {
                    if (entityItem.getItem().getId().equals(Block.BAMBOO)) {
                        if (entity.distanceSquared(e) <= rangeSquared && entity.distanceSquared(e) >= minRangeSquared) {
                            if (item == null) {
                                item = entityItem;
                            } else {
                                if (entity.distanceSquared(entityItem) < entity.distanceSquared(item)) {
                                    item = entityItem;
                                }
                            }
                        }
                    }
                }
            }
            entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_ITEM, item);
        }
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            BlockID.BAMBOO
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
                        new Behavior(entity -> {
                            entity.getMemoryStorage().put(CoreMemoryTypes.ATTACK_TARGET, ((EntityDamageByEntityEvent) entity.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT)).getDamager());
                            entity.setDataFlag(ActorFlags.ANGRY, true);
                            return true;
                        }, all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.BE_ATTACKED_EVENT),
                                entity -> entity.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT) instanceof EntityDamageByEntityEvent,
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 1),
                                entity -> getServer().getDifficulty() != 0
                        ),
                                4, 1
                        ),
                        new Behavior(
                                new LoveTimeoutExecutor(20 * 30),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                3, 1
                        ),
                        new Behavior(
                                new AnimalGrowExecutor(),
                                all(
                                        e -> e.isAgeable(),
                                        e -> e.isBaby(),
                                        e -> !e.isGrowthPaused(),
                                        e -> e.getTicksGrowLeft() > 0
                                ),
                                2, 1, 1200
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_PANDA_IDLE_AGGRESSIVE),
                                all(
                                        new RandomSoundEvaluator(),
                                        entity -> getVariant() == AGRESSIVE
                                ),
                                1, 1
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_PANDA_IDLE_WORRIED),
                                all(
                                        new RandomSoundEvaluator(),
                                        entity -> getVariant() == WORRIED,
                                        entity -> getLevel().isThundering()
                                ),
                                1, 1
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_PANDA_IDLE),
                                new RandomSoundEvaluator(),
                                1, 1
                        )
                )
                .behaviors(
                        new Behavior(
                                new PandaAttackEecutor(),
                                all(
                                        new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET),
                                        new DistanceEvaluator(CoreMemoryTypes.ATTACK_TARGET, 16)
                                ),
                                14, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                                13, 1
                        ),
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.25f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                12, 1
                        ),
                        new Behavior(
                                new EatingExecutor(),
                                entity -> !getInventory().isEmpty(),
                                11, 1
                        ),
                        new Behavior(
                                new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_ITEM, 0.4f, true),
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_ITEM),
                                10, 1
                        ),
                        new Behavior(
                                new TemptExecutor(1.25f, TEMPT_ITEMS),
                                all(
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                9, 1
                        ),
                        new Behavior(
                                new RollExecutor(),
                                all(
                                        any(
                                                entity -> getVariant() == PLAYFUL,
                                                entity -> entity.isBaby()
                                        ),
                                        new ProbabilityEvaluator(1, getVariant() == PLAYFUL ? 1300 : 16000)
                                ),
                                8, 1
                        ),
                        new Behavior(
                                new ShakeExecutor(),
                                all(
                                        entity -> getVariant() == WORRIED,
                                        entity -> getLevel().isThundering()
                                ),
                                7, 1
                        ),
                        new Behavior(
                                new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.4f, true, 2),
                                all(
                                        entity -> getVariant() == WORRIED,
                                        new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                        new DistanceEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 2.1)
                                ),
                                6, 1, 1
                        ),
                        new Behavior(
                                new SneezingExecutor(),
                                all(
                                        entity -> entity.isBaby(),
                                        new ProbabilityEvaluator(1, getVariant() == WEAK ? 500 : 6000)
                                ),
                                5, 1, 1
                        ),
                        new Behavior(
                                new LayingExecutor(10),
                                all(
                                        entity -> getVariant() == LAZY,
                                        new PandaSittingEvaluator(30)
                                ),
                                4, 2, 1
                        ),
                        new Behavior(
                                new SittingExecutor(10),
                                new PandaSittingEvaluator(30),
                                3, 2, 1
                        ),
                        new Behavior(
                                new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                                new ProbabilityEvaluator(2, 10),
                                7, 1, 100
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                                (entity -> true),
                                1, 1
                        )
                )
                .sensors(
                        new NearestPlayerSensor(16, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                entity -> (
                                        entity instanceof EntityMob mob
                                                && !(mob instanceof EntitySlime)
                                                && !(mob instanceof EntityMagmaCube)
                                                && !(mob instanceof EntityGhast)
                                                && !(mob instanceof EntityShulker)
                                                && !(mob instanceof EntityPhantom)
                                                && !(mob instanceof EntityEnderDragon)
                                )
                                        || entity instanceof Player),
                        new PandaNearestItemSensor(16, 0)
                )
                .controllers(
                        new WalkController(),
                        new LookController(true, true),
                        new FluctuateController()
                )
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

}
