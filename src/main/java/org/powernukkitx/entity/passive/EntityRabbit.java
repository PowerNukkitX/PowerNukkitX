package org.powernukkitx.entity.passive;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.HoppingController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FleeFromTargetExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.BiomeTags;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityRabbit extends EntityAnimal implements EntityWalkable, EntityVariant {
    public static final int COAT_BROWN = 0;
    public static final int COAT_WHITE = 1;
    public static final int COAT_BLACK = 2;
    public static final int COAT_SPLOTCHED = 3;
    public static final int COAT_DESERT = 4;
    public static final int COAT_SALT = 5;

    private static final int[] VARIANTS = {
            COAT_BROWN,
            COAT_WHITE,
            COAT_BLACK,
            COAT_SPLOTCHED,
            COAT_DESERT,
            COAT_SALT
    };

    @Override
    @NotNull public String getIdentifier() {
        return RABBIT;
    }


    public EntityRabbit(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.402f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.268f;
        }
        return 0.402f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(3);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public String getOriginalName() {
        return "Rabbit";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("rabbit", "lightweight", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    ItemID.GOLDEN_CARROT,
                    ItemID.CARROT,
                    BlockID.DANDELION
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.RABBIT, EntityID.RABBIT)
                ),
                null,
                null,
                null,
                null,
                null,
                new BreedableComponent.MutationFactor(0.2f, null, null),
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
                    new AgeableComponent.FeedItem(ItemID.GOLDEN_CARROT),
                    new AgeableComponent.FeedItem(ItemID.CARROT),
                    new AgeableComponent.FeedItem(BlockID.DANDELION)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        if (Utils.rand(0, 1) == 0) {
            int amount = Utils.rand(0, 1 + looting);
            if (amount > 0) {
                drops.add(Item.get(Item.RABBIT_HIDE, 0, amount));
            }
        }

        if (Utils.rand(0, 1) == 0) {
            int amount = Utils.rand(0, 1 + looting);
            if (amount > 0) {
                drops.add(Item.get(
                        this.isOnFire() ? Item.COOKED_RABBIT : Item.RABBIT,
                        0,
                        amount
                ));
            }
        }

        float footChance = 0.10f + (0.03f * looting);
        if (Utils.rand(0f, 1f) < footChance) {
            drops.add(Item.get(Item.RABBIT_FOOT));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (!hasVariant()) {
            setVariant(getBiomeVariant());
        }
        if (this.isBaby()) {
            this.setScale(0.4f);
        } else this.setScale(0.60f);
    }

    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    private int getBiomeVariant() {
        var biomeTags = Registries.BIOME.getTags(getLevel().getBiomeId((int) x, (int) y, (int) z));
        if (biomeTags.contains(BiomeTags.DESERT)) {
            return COAT_DESERT;
        }
        if (biomeTags.contains(BiomeTags.FROZEN) || biomeTags.contains(BiomeTags.ICE) || biomeTags.contains(BiomeTags.SNOWY_SLOPES)) {
            return Utils.rand(1, 100) <= 80 ? COAT_WHITE : COAT_SPLOTCHED;
        }

        int roll = Utils.rand(1, 100);
        if (roll <= 50) return COAT_BROWN;
        if (roll <= 90) return COAT_BLACK;
        return COAT_SALT;
    }

    private boolean shouldAvoid(Entity entity) {
        if (entity instanceof Player player && !player.isSurvival()) {
            return false;
        }
        return entity instanceof Player || entity.isFamily("wolf") || entity.isFamily("monster");
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.GOLDEN_CARROT,
        ItemID.CARROT,
        BlockID.DANDELION
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
                    )
                ),
                Set.of(
                    new Behavior(
                        entity -> {
                            if (entity.getMemoryStorage().get(CoreMemoryTypes.BE_ATTACKED_EVENT) instanceof EntityDamageByEntityEvent event) {
                                entity.getMemoryStorage().put(CoreMemoryTypes.NEAREST_SHARED_ENTITY, event.getDamager());
                            }
                            return false;
                        },
                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                        8, 1
                    ),
                    new Behavior(
                        new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 0.66f, true, 8),
                        all(
                            new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100)
                        ),
                        7, 1
                    ),
                    new Behavior(
                        new FleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY, 1f, true, 8),
                        all(
                            new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                            e -> shouldAvoid(e.getMemoryStorage().get(CoreMemoryTypes.NEAREST_SHARED_ENTITY))
                        ),
                        4, 1
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.4f, 12, 10, true, 100, true, 10),
                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 100, 100),
                        3, 1
                    ),
                    new Behavior(
                        new BreedingExecutor(16, 200, 0.25f),
                            all(
                                e -> !e.isBaby(),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                            ),
                        6, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.0f, TEMPT_ITEMS),
                            all(
                                e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                            ),
                        5, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            new ProbabilityEvaluator(4, 10),
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                            (entity -> true),
                        1, 1
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20),
                    new NearestTargetEntitySensor<>(0, 8, 20, List.of(CoreMemoryTypes.NEAREST_SHARED_ENTITY), this::shouldAvoid)
                ),
                Set.of(
                    new HoppingController(15),
                    new LookController(() -> !isMoving(), this::isMoving),
                    new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
