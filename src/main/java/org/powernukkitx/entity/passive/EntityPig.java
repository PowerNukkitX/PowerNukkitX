package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
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
import org.powernukkitx.entity.ai.evaluator.RiderItemControllableEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FollowRiderExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BoostableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.RideableComponent;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.math.Vector3f;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author BeYkeRYkt (Nukkit Project)
 */
public class EntityPig extends EntityAnimal implements EntityWalkable, ClimateVariant {
    private static final String[] CLIMATE_VARIANTS = {
        "temperate",
        "warm",
        "cold"
    };

    private static final String[] SOUND_VARIANTS = {
        "default",
        "big",
        "mini"
    };

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new EnumEntityProperty("minecraft:climate_variant", CLIMATE_VARIANTS, "temperate", true),
        new EnumEntityProperty("minecraft:sound_variant", SOUND_VARIANTS, "default", true)
    };

    public EntityPig(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    @NotNull public String getIdentifier() {
        return PIG;
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public boolean isRideable() {
        return !this.isBaby();
    }

    @Override
    public boolean requireSaddleToMount() {
        return true;
    }

    @Override
    public @Nullable RideableComponent getComponentRideable() {
        boolean crounchingSkipInteract = this.isSaddled();
        Set<String> riders = crounchingSkipInteract ? Set.of("player") : Set.of("baby_zombie", "baby_husk");
        float yOffset = crounchingSkipInteract ? 0.63f : 0.7f;
        String interectText = crounchingSkipInteract ? "action.interact.ride.horse" : "";

        return new RideableComponent(
                0,
                crounchingSkipInteract,
                RideableComponent.DismountMode.DEFAULT,
                riders,
                interectText,
                0.0f,
                false,
                false,
                1,
                List.of(new RideableComponent.Seat(
                        0,
                        1,
                        new Vector3f(0.0f, yOffset, 0.0f),
                        null,
                        null,
                        null,
                        null
                ))
        );
    }

    @Override
    public boolean canBeSaddled() {
        return true;
    }

    @Override
    public String getItemControllable() {
        return Item.CARROT_ON_A_STICK;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(10);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.25f);
    }

    @Override
    public String getOriginalName() {
        return "Pig";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("pig", "mob");
    }

    @Override
    public String[] getSoundVariants() {
        return SOUND_VARIANTS;
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                Set.of(
                    ItemID.CARROT,
                    ItemID.POTATO,
                    BlockID.BEETROOT
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.PIG, EntityID.PIG)
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
                    new AgeableComponent.FeedItem(ItemID.CARROT),
                    new AgeableComponent.FeedItem(ItemID.POTATO),
                    new AgeableComponent.FeedItem(BlockID.BEETROOT)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public @Nullable BoostableComponent getComponentBoostable() {
        return new BoostableComponent(
            1.35f,
            3.0f,
            List.of(
                    new BoostableComponent.BoostItem(
                        ItemID.CARROT_ON_A_STICK,
                        2,
                        ItemID.FISHING_ROD
                    )
            )
        );
    }

    @Override
    public void initEntity() {
        super.initEntity();

        if(nbt.contains("variant")) {
            setVariant(Variant.get(getNbt().getString("variant")));
        } else setVariant(getBiomeVariant(getLevel().getBiomeId((int) x, (int) y, (int) z)));

        this.initSoundVariantProperty();
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        List<Item> drops = new ArrayList<>();

        int amount = Utils.rand(1, 3 + looting);
        Item porkchop = Item.get(this.isOnFire() ? Item.COOKED_PORKCHOP : Item.PORKCHOP, 0, amount);

        drops.add(porkchop);

        if (isSaddled()) {
            drops.add(Item.get(Item.SADDLE, 0, 1));
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (this.isBaby()) return false;

        if (!item.isNull()) {
            if (item.getId() == Item.SADDLE && !this.isSaddled()) {
                if (!player.isCreative()) player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                getLevel().addLevelSoundEvent(this, SoundEvent.SADDLE, -1, getIdentifier(), false, false);
                setSaddle(true);
                return true;

            } else if (item.getId() == Item.SHEARS && this.isSaddled()) {
                Item saddleItem = Item.get(Item.SADDLE, 0, 1);
                if (player.getInventory().canAddItem(saddleItem)) {
                    player.getInventory().addItem(saddleItem);
                } else {
                    this.getLevel().dropItem(clickedPos, saddleItem);
                }
                setSaddle(false);
                return false;
            }
        }

        if (isSaddled()) mountEntity(player, true);
        return false;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.POTATO,
        ItemID.CARROT,
        ItemID.CARROT_ON_A_STICK,
        BlockID.BEETROOT
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
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
                        1, 1, 1200
                    )
                ),
                Set.of(
                    new Behavior(
                        new PlaySoundExecutor(Sound.MOB_PIG_SAY),
                            new RandomSoundEvaluator(),
                        8,1
                    ),
                    new Behavior(
                        new FollowRiderExecutor(),
                            new RiderItemControllableEvaluator(),
                        7, 1
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
                        new FlatRandomRoamExecutor(this.getMovementSpeedDefault() * 1.25f, 18, 8, true, 80, true, 10),
                            all(
                                e -> e.passengers.isEmpty(),
                                new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 80)
                            ),
                        5, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.2f, TEMPT_ITEMS),
                            all(
                                e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                            ),
                        3, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            all(
                                new ProbabilityEvaluator(4, 10),
                                e -> e.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_PLAYER),
                                e -> {
                                    Player p = e.getMemoryStorage().get(CoreMemoryTypes.NEAREST_PLAYER);
                                    return p != null && !e.isPassenger(p);
                                },
                                e -> e.passengers == null || e.passengers.isEmpty()
                            ),
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(this.getMovementSpeedDefault(), 12, 100, false, -1, true, 10),
                            (entity -> true),
                        1, 1
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20)),
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
