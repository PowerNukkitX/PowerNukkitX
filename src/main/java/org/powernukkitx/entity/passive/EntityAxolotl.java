package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCanAttack;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.ConditionalController;
import org.powernukkitx.entity.ai.controller.DiveController;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.ConditionalAStarRouteFinder;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.effect.Effect;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EntityAxolotl extends EntityAnimal implements EntitySwimmable, EntityVariant, EntityCanAttack {
    @Override
    @NotNull
    public String getIdentifier() {
        return AXOLOTL;
    }

    private final static int[] VARIANTS = new int[]{0, 1, 2, 3, 4};

    private final static float[] DIFF_DAMAGE = new float[]{2, 2, 2};

    public EntityAxolotl(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            ItemID.TROPICAL_FISH_BUCKET
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior(
                                new LoveTimeoutExecutor(20 * 30),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                2, 1
                        ),
                        new Behavior(
                                new AnimalGrowExecutor(),
                                all(
                                        e -> e.isAgeable(),
                                        e -> e.isBaby(),
                                        e -> !e.isGrowthPaused(),
                                        e -> e.getTicksGrowLeft() > 0
                                ),
                                1, 1, 1200
                        ),
                        new Behavior(entity -> {
                            setMoveTarget(getMemoryStorage().get(CoreMemoryTypes.NEAREST_BLOCK));
                            return true;
                        },
                                all(
                                        new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK),
                                        entity -> !isInsideOfWater(),
                                        not(new DistanceEvaluator(CoreMemoryTypes.NEAREST_BLOCK, 9))
                                ),
                                1, 1
                        )
                ),
                Set.of(
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_AXOLOTL_SPLASH), all(
                                entity -> getAirTicks() == 399
                        ),
                                8, 1
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_AXOLOTL_IDLE_WATER),
                                all(
                                        new RandomSoundEvaluator(), entity -> isInsideOfWater()
                                ),
                                7, 1
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_AXOLOTL_IDLE),
                                all(new RandomSoundEvaluator(), entity -> !isInsideOfWater()
                                ),
                                6, 1
                        ),
                        new Behavior(
                                new MeleeAttackExecutor(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET, 0.3f, 17, true, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET),
                                5, 1
                        ),
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.35f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                4, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10), new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                                3, 1
                        ),
                        new Behavior(
                                new TemptExecutor(1.1f, TEMPT_ITEMS),
                                all(
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                2, 1
                        ),
                        new Behavior(
                                new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), new ProbabilityEvaluator(4, 10),
                                1, 1, 100
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, false, 10),
                                entity -> !entity.isInsideOfWater(),
                                1, 1
                        ),
                        new Behavior(
                                new SpaceRandomRoamExecutor(0.36f, 12, 1, 80, false, -1, false, 10),
                                Entity::isInsideOfWater,
                                1, 1
                        )
                ),
                Set.of(
                        new NearestPlayerSensor(8, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new BlockSensor(BlockFlowingWater.class, CoreMemoryTypes.NEAREST_BLOCK, 16, 5, 10),
                        entity -> {
                            if (getLevel().getTick() % 20 == 0) {
                                Entity lastAttack = getMemoryStorage().get(CoreMemoryTypes.LAST_ATTACK_ENTITY);
                                if (lastAttack != null) {
                                    if (!lastAttack.isAlive()) {
                                        if (lastAttack instanceof EntityIntelligent intelligent) {
                                            if (intelligent.getLastDamageCause() instanceof EntityDamageByEntityEvent event) {
                                                if (event.getDamager() instanceof Player player) {
                                                    player.removeEffect(EffectType.MINING_FATIGUE);
                                                    player.addEffect(Effect.get(EffectType.REGENERATION).setDuration((player.hasEffect(EffectType.REGENERATION) ? player.getEffect(EffectType.REGENERATION).getDuration() : 0) + 100));
                                                }
                                            }
                                        }
                                        getMemoryStorage().clear(CoreMemoryTypes.LAST_ATTACK_ENTITY);
                                    }
                                }
                            }
                        }
                ),
                Set.of(
                        new LookController(true, true),
                        new ConditionalController(
                                Pair.of(Entity::isInsideOfWater, new DiveController()),
                                Pair.of(Entity::isInsideOfWater, new SpaceMoveController()),
                                Pair.of(entity -> !entity.isInsideOfWater(), new WalkController()),
                                Pair.of(entity -> !entity.isInsideOfWater(), new FluctuateController())
                        )
                ),
                new ConditionalAStarRouteFinder(
                        this,
                        Pair.of(ent -> !ent.isInsideOfWater(), new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)),
                        Pair.of(Entity::isInsideOfWater, new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this)
                        )
                ),
                this
        );
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if(Objects.equals(item.getId(), Item.WATER_BUCKET)) {
            Item bucket = Item.get(Item.AXOLOTL_BUCKET);
            CompoundTag tag = new CompoundTag();
            tag.putInt("Variant", getVariant());
            bucket.setNbt(tag);
            player.getInventory().setItemInMainHand(bucket);
            this.close();
        }
        return superResult;
    }

    @Override
    public float getHeight() {
        return 0.42f;
    }

    @Override
    public float getWidth() {
        return 0.75f;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && getLevelBlock().canPassThrough()) {
            if (getAirTicks() > -5600 || getLevel().isRaining() || getLevel().isThundering()) return false;
        }
        return super.attack(source);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (!hasVariant()) {
            setVariant(randomVariant());
        }
    }

    @Override
    public String getOriginalName() {
        return "Axolotl";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("axolotl", "mob");
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(14);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                        ItemID.TROPICAL_FISH_BUCKET
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.AXOLOTL, EntityID.AXOLOTL)
                ),
                null,
                null,
                null,
                null,
                false,
                new BreedableComponent.MutationFactor(0.00083f, null, null),
                null,
                false,
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
                        new AgeableComponent.FeedItem(ItemID.TROPICAL_FISH_BUCKET, 0.05f)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public int randomVariant() {
        if (Utils.rand(0, 1200) == 0) return VARIANTS[VARIANTS.length - 1];
        return VARIANTS[Utils.rand(0, VARIANTS.length - 2)];
    }

    @Override
    public float[] getDiffHandDamage() {
        return DIFF_DAMAGE;
    }

    @Override
    public Integer getExperienceDrops() {
        return 1;
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.COD,
                 Entity.ELDER_GUARDIAN,
                 Entity.GLOW_SQUID,
                 Entity.GUARDIAN,
                 Entity.PUFFERFISH,
                 Entity.SALMON,
                 Entity.TADPOLE,
                 Entity.TROPICALFISH,
                 Entity.DROWNED -> true;
            default -> false;
        };
    }
}
