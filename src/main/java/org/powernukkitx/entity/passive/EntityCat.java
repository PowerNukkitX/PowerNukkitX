package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityCanAttack;
import org.powernukkitx.entity.EntityCanSit;
import org.powernukkitx.entity.EntityColor;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityVariant;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.ConditionalProbabilityEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.EntityMoveToOwnerExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.SleepOnOwnerBedExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.entity.components.TameableComponent;
import org.powernukkitx.entity.data.property.EntityProperty;
import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemDye;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;
import org.powernukkitx.utils.Utils;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;


public class EntityCat extends EntityAnimal implements EntityWalkable, EntityCanSit, EntityCanAttack, EntityVariant, EntityColor {
    private static final String[] SOUND_VARIANTS = {
            "default",
            "royal"
    };

    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
            new EnumEntityProperty("minecraft:sound_variant", SOUND_VARIANTS, "default", true)
    };

    @Override
    @NotNull
    public String getIdentifier() {
        return CAT;
    }

    // There are 11 color variations in cats.
    private static final int[] VARIANTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    protected float[] diffHandDamage = new float[]{4, 4, 4};

    public EntityCat(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void updateMovement() {
        // How could a cat's streamlined movement possibly cause it to fall and get injured?
        this.highestPosition = this.y;
        super.updateMovement();
    }

    // Cat body sizes from Wiki https://minecraft.wiki/w/Cat
    @Override
    public float getWidth() {
        return this.isBaby() ? 0.24f : 0.48f;
    }

    @Override
    public float getHeight() {
        return this.isBaby() ? 0.28f : 0.56f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(10);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.3f);
    }

    @Override
    public @Nullable TameableComponent getComponentTameable() {
        return new TameableComponent(
                0.33f,
                Set.of(
                        ItemID.COD,
                        ItemID.SALMON
                )
        );
    }

    //Attack Selectors
    //Stray cats will attack rabbits and baby turtles
    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case RABBIT, TURTLE -> true;
            default -> false;
        };
    }

    @Override
    public boolean onUpdate(int currentTick) {
        // Synchronize owner eid
        if (hasOwner()) {
            Player owner = getOwner();
            if (owner != null && getDataProperty(ActorDataTypes.OWNER) != owner.getId()) {
                this.setDataProperty(ActorDataTypes.OWNER, owner.getId());
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public void initEntity() {
        super.initEntity();
        this.setDataProperty(ActorDataTypes.AMBIENT_SOUND_EVENT_NAME, "ambient");
        if (!hasVariant()) {
            this.setVariant(randomVariant());
        }
        // Update collar color
        if (this.nbt.contains("Color")) {
            this.setColor(DyeColor.getByWoolData(this.getNbt().getByte("Color")));
        }

        this.initSoundVariantProperty();
    }

    // Cats have 11 colour variants
    @Override
    public int[] getAllVariant() {
        return VARIANTS;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (!item.isNull() && this.isTamed() && (item instanceof ItemDye dyeItem) && this.hasOwner() && player.equals(this.getOwner())) {
            player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
            this.setColor(dyeItem.getDyeColor());
            return true;
        }

        if (this.hasOwner() && player.getName().equals(getOwnerName()) && !this.isTouchingWater()) {
            this.setSitting(!this.isSitting());
            return false;
        }

        return false;
    }

    // Killing cats yields 0-2 strands of thread
    // Killing kittens yields nothing
    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        if (!this.isBaby()) {
            int catdrops = Utils.rand(0, 2);
            if (catdrops > 0)
                return new Item[]{Item.get(Item.STRING, 0, catdrops)};
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public String getOriginalName() {
        return "Cat";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("cat", "mob");
    }

    @Override
    public String[] getSoundVariants() {
        return SOUND_VARIANTS;
    }

    @Override
    public void onTameSuccess(Player player) {
        this.setHealthMax(20);
        this.setHealthCurrent(20);
        if (!this.hasColor()) {
            this.setColor(DyeColor.RED);
        }
        this.getLevel().dropExpOrb(this, Utils.rand(1, 7));
        super.onTameSuccess(player);
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                true,
                null,
                null,
                Set.of(
                        ItemID.COD,
                        ItemID.SALMON
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.CAT, EntityID.CAT)
                ),
                null,
                null,
                null,
                null,
                true,
                null,
                true,
                true,
                true,
                null
        );
    }

    @Override
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                        new HealableComponent.Item(ItemID.COD, 2),
                        new HealableComponent.Item(ItemID.SALMON, 2)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                1200f,
                List.of(
                        new AgeableComponent.FeedItem(ItemID.COD),
                        new AgeableComponent.FeedItem(ItemID.SALMON)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public float[] getDiffHandDamage() {
        return diffHandDamage;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
            ItemID.COD,
            ItemID.SALMON
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        new Behavior( // Untamed cats will seek out and attack rabbits and baby turtles within 15 blocks
                                entity -> {
                                    if (this.hasOwner(false)) return false;
                                    var storage = getMemoryStorage();
                                    if (storage.notEmpty(CoreMemoryTypes.ATTACK_TARGET)) return false;
                                    Entity attackTarget = null;
                                    if (storage.notEmpty(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET)
                                            && storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET).isAlive()) {
                                        attackTarget = storage.get(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET);
                                    }
                                    storage.put(CoreMemoryTypes.ATTACK_TARGET, attackTarget);
                                    return false;
                                },
                                entity -> true,
                                20
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
                        new Behavior( // Refresh random playback sound effects. The sounds a cat makes when it is tamed.
                                (entity) -> {

                                    if (this.hasOwner(false))
                                        this.setAmbientSoundEvent(Sound.MOB_CAT_MEOW);
                                    else
                                        this.setAmbientSoundEvent(Sound.MOB_CAT_STRAYMEOW);
                                    return false;
                                },
                                (entity) -> true,
                                1, 1, 20
                        )
                ),
                Set.of(
                        new Behavior( // Sleep Priority 7
                                new SleepOnOwnerBedExecutor(),
                                entity -> {
                                    if (this.isSitting()) return false; // sitting should block sleeping pathing
                                    var player = this.getOwner();
                                    if (player == null) return false;
                                    if (player.getLevel().getId() != this.level.getId()) return false;
                                    return player.isSleeping();
                                },
                                7
                        ),
                        // Attack the target with the highest aggro. Priority 6
                        new Behavior(
                                new MeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.35f, 15, true, 10),
                                all(
                                        e -> !this.isSitting(),
                                        new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.ATTACK_TARGET)
                                ),
                                6
                        ),
                        // Cat breeding priority 5
                        new Behavior(
                                new BreedingExecutor(16, 200, 0.35f),
                                all(
                                        e -> !this.isSitting(),
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                5
                        ),
                        // Cat moves toward owner (Priority 4)
                        new Behavior(
                                new EntityMoveToOwnerExecutor(0.35f, true, 15),
                                entity -> {
                                    if (this.isSitting()) return false;
                                    if (this.hasOwner()) {
                                        var player = getOwner();
                                        if (!player.isOnGround()) return false;
                                        var distanceSquared = entity.distanceSquared(player);
                                        return distanceSquared >= 100;
                                    } else return false;
                                },
                                4
                        ),
                        new Behavior(
                                new TemptExecutor(0.5f, false, false, true, 16, 2.0f, new TemptExecutor.TemptSound("tempt", 0.0f, 100.0f), TEMPT_ITEMS),
                                all(
                                        e -> !e.isSitting(),
                                        e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                        e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                                ),
                                3, 1
                        ),
                        // The cat moves randomly around its owner. Priority 3
                        new Behavior(
                                new FlatRandomRoamExecutor(0.1f, 4, 100, false, -1, true, 20),
                                all(
                                        e -> !this.isSitting(),
                                        e -> this.hasOwner() && this.getOwner().distanceSquared(this) < 100
                                ),
                                2
                        ),
                        // Cat moves to random target point. Priority 1
                        new Behavior(
                                new FlatRandomRoamExecutor(0.2f, 12, 150, false, -1, true, 20),
                                all(
                                        e -> !this.isSitting(),
                                        new ProbabilityEvaluator(5, 10)
                                ),
                                1, 1, 25
                        ),
                        // The cat looks at the target player. Priority 1.
                        // NOTE: do NOT gate with sitting for the same reason as feeding look.
                        new Behavior(
                                new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                                new ConditionalProbabilityEvaluator(3, 7, entity -> hasOwner(false), 10),
                                1, 1, 25
                        )
                ),
                Set.of(
                        new NearestPlayerSensor(8, 0, 20),
                        new NearestTargetEntitySensor<>(0, 15, 20, List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget)
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
