package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityShearable;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.ParticleEffect;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.Vector3;
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
// TODO: Variantes logic
public class EntityMooshroom extends EntityAnimal implements EntityWalkable, EntityShearable {
    @Override
    @NotNull public String getIdentifier() {
        return MOOSHROOM;
    }
    

    public EntityMooshroom(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.45f;
        }
        return 0.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.65f;
        }
        return 1.3f;
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
        return "Mooshroom";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("mushroomcow", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    BlockID.WHEAT
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.MOOSHROOM, EntityID.MOOSHROOM)
                ),
                null,
                new BreedableComponent.DenyParentsVariant(0.00098f, 0, 1),
                null,
                null,
                null,
                null,
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
                    new AgeableComponent.FeedItem(BlockID.WHEAT)
                ),
                null,
                null,
                null
        );
    }

    @Override
    public Item[] getDrops(@NotNull Item weapon) {
        List<Item> drops = new ArrayList<>();

        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);

        int meatAmount = Utils.rand(1, 3 + looting);
        drops.add(Item.get(
                this.isOnFire() ? Item.COOKED_BEEF : Item.BEEF,
                0,
                meatAmount
        ));

        if (Utils.rand(0, 2) != 0) {
            int leatherAmount = Utils.rand(0, 2 + looting);
            if (leatherAmount > 0) {
                drops.add(Item.get(Item.LEATHER, 0, leatherAmount));
            }
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if (item.getId().equals(Item.SHEARS) && item.useOn(this)) {
            shear();
            return true;
        } else if (item.getId().equals(Item.BUCKET) && item.getDamage() == 0) {
            item.count--;
            player.getInventory().addItem(Item.get(Item.BUCKET, 1));
            return true;
        } else if (item.getId().equals(Item.BOWL) && item.getDamage() == 0) {
            item.count--;
            player.getInventory().addItem(Item.get(Item.MUSHROOM_STEW));
            return true;
        }

        return false;
    }

    @Override
    public boolean shear() {
        this.close();
        this.level.dropItem(this, Item.get(BlockID.RED_MUSHROOM, 0, 5));
        this.level.addSound(this, Sound.MOB_MOOSHROOM_CONVERT);
        this.level.addParticleEffect(this.add(0, this.getHeight(), 0), ParticleEffect.LARGE_EXPLOSION_LEVEL);
        EntityCow cow = new EntityCow(this.getChunk(), this.getNbt());
        cow.setPosition(this);
        cow.setHealthCurrent(this.health);
        cow.setRotation(this.yaw, this.pitch);
        cow.spawnToAll();
        this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.getVector3(), VibrationType.SHEAR));
        return true;
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        BlockID.WHEAT
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return BehaviorGroup.builder(this)
                .coreBehaviors(
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
                )
                .behaviors(
                    new Behavior(
                        new FlatRandomRoamExecutor(0.25f, 12, 40, true, 100, true, 10),
                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                        4, 1
                    ),
                    new Behavior(
                        new BreedingExecutor(16, 200, 0.25f),
                            all(
                                e -> !e.isBaby(),
                                e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                            ),
                        3, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.25f, TEMPT_ITEMS),
                            all(
                                e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                            ),
                        2, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            new ProbabilityEvaluator(4, 10),
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.1f, 12, 100, false, -1, true, 10),
                            (entity -> true),
                        1, 1
                    )
                )
                .sensors(
                    new NearestPlayerSensor(8, 0, 20)
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
