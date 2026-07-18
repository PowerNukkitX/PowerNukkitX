package org.powernukkitx.entity.passive;

import org.powernukkitx.entity.ClimateVariant;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityChicken extends EntityAnimal implements EntityWalkable, ClimateVariant {
    private static final String[] CLIMATE_VARIANTS = {
            "temperate",
            "warm",
            "cold"
    };

    private static final String[] SOUND_VARIANTS = {
            "default",
            "picky"
    };

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
            new EnumEntityProperty("minecraft:climate_variant", CLIMATE_VARIANTS, "temperate", true),
            new EnumEntityProperty("minecraft:sound_variant", SOUND_VARIANTS, "default", true)
    };

    @Override
    @NotNull
    public String getIdentifier() {
        return CHICKEN;
    }

    public EntityChicken(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void updateMovement() {
        // Supplement the chicken's slow, damage-free landing characteristic
        if (!this.onGround && this.motionY < -0.08f) {
            this.motionY = -0.08f;
            this.highestPosition = this.y;
        }
        super.updateMovement();
    }

    private Item getEgg() {
        if (getVariant() == Variant.COLD) return Item.get(Item.BLUE_EGG);
        if (getVariant() == Variant.WARM) return Item.get(Item.BROWN_EGG);
        return Item.get(Item.EGG);
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
            return 0.4f;
        }
        return 0.8f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(4);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                Set.of(
                        ItemID.WHEAT_SEEDS,
                        ItemID.BEETROOT_SEEDS,
                        ItemID.MELON_SEEDS,
                        ItemID.PUMPKIN_SEEDS,
                        ItemID.PITCHER_POD,
                        ItemID.TORCHFLOWER_SEEDS
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.CHICKEN, EntityID.CHICKEN)
                ),
                false
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                        new AgeableComponent.FeedItem(ItemID.WHEAT_SEEDS),
                        new AgeableComponent.FeedItem(ItemID.BEETROOT_SEEDS),
                        new AgeableComponent.FeedItem(ItemID.MELON_SEEDS),
                        new AgeableComponent.FeedItem(ItemID.PUMPKIN_SEEDS),
                        new AgeableComponent.FeedItem(ItemID.PITCHER_POD),
                        new AgeableComponent.FeedItem(ItemID.TORCHFLOWER_SEEDS)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public String getOriginalName() {
        return "Chicken";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("chicken", "mob");
    }

    @Override
    public String[] getSoundVariants() {
        return SOUND_VARIANTS;
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        List<Item> drops = new ArrayList<>();

        int chickenAmount = Utils.rand(1, 1 + looting);
        drops.add(Item.get(
                this.isOnFire() ? Item.COOKED_CHICKEN : Item.CHICKEN,
                0,
                chickenAmount
        ));

        int featherAmount = Utils.rand(0, 2 + looting);
        if (featherAmount > 0) {
            drops.add(Item.get(Item.FEATHER, 0, featherAmount));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (nbt.contains("variant")) {
            setVariant(Variant.get(getNbt().getString("variant")));
        } else setVariant(getBiomeVariant(getLevel().getBiomeId((int) x, (int) y, (int) z)));

        this.initSoundVariantProperty();
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            ItemID.WHEAT_SEEDS,
            ItemID.BEETROOT_SEEDS,
            ItemID.MELON_SEEDS,
            ItemID.PUMPKIN_SEEDS,
            ItemID.PITCHER_POD,
            ItemID.TORCHFLOWER_SEEDS
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
                                new PlaySoundExecutor(Sound.MOB_CHICKEN_SAY),
                                new RandomSoundEvaluator(),
                                7, 1
                        ),
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.35f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                6
                        ),
                        new Behavior(
                                new TemptExecutor(1.0f, TEMPT_ITEMS),
                                all(
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                3, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.22f, 12, 40, true, 100, true, 10),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                                4, 1
                        ),
                        new Behavior(
                                new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                                new ProbabilityEvaluator(4, 10),
                                1, 1, 100
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.22f, 12, 100, false, -1, true, 10),
                                (entity -> true),
                                1, 1
                        ),
                        new Behavior(
                                entity -> {
                                    entity.getMemoryStorage().put(CoreMemoryTypes.LAST_EGG_SPAWN_TIME, getLevel().getTick());
                                    entity.getLevel().dropItem(entity, getEgg());
                                    entity.getLevel().addSound(entity, Sound.MOB_CHICKEN_PLOP);
                                    return false;
                                },
                                any(
                                        all(
                                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_EGG_SPAWN_TIME, 6000, 12000),
                                                new ProbabilityEvaluator(20, 100)
                                        ),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_EGG_SPAWN_TIME, 12000, Integer.MAX_VALUE)
                                ),
                                1, 1, 20
                        )
                ),
                Set.of(
                        new NearestPlayerSensor(8, 0, 20)
                ),
                Set.of(
                        new WalkController(),
                        new LookController(true, true),
                        new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
