package org.powernukkitx.entity.mob;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockPortal;
import org.powernukkitx.block.BlockRespawnAnchor;
import org.powernukkitx.block.BlockWarpedFungus;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.DistanceEvaluator;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.FleeFromTargetExecutor;
import org.powernukkitx.entity.ai.executor.HoglinTransformExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.MeleeAttackExecutor;
import org.powernukkitx.entity.ai.executor.MoveToTargetExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.memory.MemoryType;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;

import org.cloudburstmc.protocol.bedrock.data.SoundEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Erik Miller | EinBexiii
 */
public class EntityHoglin extends EntityMob implements EntityWalkable {
    @Override
    @NotNull
    public String getIdentifier() {
        return HOGLIN;
    }

    public EntityHoglin(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public boolean isAngry() {
        return getDataFlag(ActorFlags.ANGRY);
    }

    @Override
    protected void initEntity() {
        this.diffHandDamage = new float[]{1f, 1f, 1f};
        super.initEntity();
    }

    @Override
    public float[] getDiffHandDamage() {
        if (isBaby()) {
            return super.getDiffHandDamage();
        } else return new float[]{
                Utils.rand(2.5f, 5f),
                Utils.rand(3f, 8f),
                Utils.rand(4.5f, 12f),
        };
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.85f;
        }
        return 1.4f;
    }

    @Override
    public String getOriginalName() {
        return "Hoglin";
    }

    @Override
    public Set<String> typeFamily() {
        if (this.isBaby()) {
            return Set.of("hoglin", "hoglin_baby", "mob");
        }
        return Set.of("hoglin", "hoglin_adult", "mob");
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(40);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.36f);
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                Set.of(
                        BlockID.CRIMSON_FUNGUS
                ),
                List.of(
                        new BreedableComponent.BreedsWith(EntityID.HOGLIN, EntityID.HOGLIN)
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
                        new AgeableComponent.FeedItem(BlockID.CRIMSON_FUNGUS)
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

        int porkAmount = Utils.rand(2, 4 + looting);
        drops.add(Item.get(
                this.isOnFire() ? Item.COOKED_PORKCHOP : Item.PORKCHOP,
                0,
                porkAmount
        ));

        if (Utils.rand(0, 1) == 1) {
            int leatherAmount = Utils.rand(0, 1 + looting);
            if (leatherAmount > 0) {
                drops.add(Item.get(Item.LEATHER, 0, leatherAmount));
            }
        }

        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public Integer getExperienceDrops() {
        return isBaby() ? 0 : Utils.rand(1, 3);
    }

    protected static class HoglinFleeFromTargetExecutor extends FleeFromTargetExecutor {

        public HoglinFleeFromTargetExecutor(MemoryType<? extends Vector3> memory) {
            super(memory, 0.5f, true, 8);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            if (entity.distance(entity.getMemoryStorage().get(getMemory())) < 8) {
                entity.getLevel().addSound(entity, Sound.MOB_HOGLIN_RETREAT);
            }
        }
    }

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
                                new BreedingExecutor(16, 200, 0.35f),
                                all(
                                        e -> !e.isBaby(),
                                        e -> e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE)
                                ),
                                9, 1
                        ),
                        new Behavior(
                                new HoglinTransformExecutor(),
                                all(
                                        entity -> entity.getLevel().getDimension() != Level.DIMENSION_NETHER,
                                        entity -> !isImmobile(),
                                        entity -> !entity.getNbt().getBoolean("IsImmuneToZombification")
                                ),
                                8, 1
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_HOGLIN_ANGRY, 0.8f, 1.2f, 0.8f, 0.8f),
                                all(
                                        new RandomSoundEvaluator(),
                                        entity -> isAngry()
                                ),
                                7, 1
                        ),
                        new Behavior(
                                new PlaySoundExecutor(Sound.MOB_HOGLIN_AMBIENT, 0.8f, 1.2f, 0.8f, 0.8f),
                                all(
                                        new RandomSoundEvaluator(),
                                        entity -> !isAngry()
                                ),
                                6, 1
                        ),
                        new Behavior(
                                new HoglinMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 40, true, 30),
                                all(
                                        new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET)
                                ),
                                5, 1
                        ),
                        new Behavior(
                                new HoglinMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 40, false, 30),
                                all(
                                        new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER)
                                ),
                                4, 1
                        ),
                        new Behavior(
                                new HoglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_BLOCK),
                                all(
                                        new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_BLOCK)
                                ),
                                3, 1
                        ),
                        new Behavior(
                                new MoveToTargetExecutor(CoreMemoryTypes.PARENT, this.getMovementSpeedDefault() * 1.9f, true, 64, 3),
                                all(
                                        new EntityCheckEvaluator(CoreMemoryTypes.PARENT),
                                        new DistanceEvaluator(CoreMemoryTypes.PARENT, 5),
                                        entity -> isBaby()
                                ),
                                2, 1
                        ),
                        new Behavior(
                                new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10),
                                none(),
                                1, 1
                        )
                )
                .sensors(
                        new NearestPlayerSensor(40, 0, 20),
                        new BlockSensor(BlockPortal.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20),
                        new BlockSensor(BlockWarpedFungus.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20),
                        new BlockSensor(BlockRespawnAnchor.class, CoreMemoryTypes.NEAREST_BLOCK, 8, 2, 20)
                )
                .controllers(
                        new WalkController(),
                        new LookController(true, true)
                )
                .routeFinder(new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this))
                .build();
    }

    protected static class HoglinMeleeAttackExecutor extends MeleeAttackExecutor {
        public HoglinMeleeAttackExecutor(MemoryType<? extends Entity> memory, float speed, int maxSenseRange, boolean clearDataWhenLose, int coolDown) {
            super(memory, speed, maxSenseRange, clearDataWhenLose, coolDown);
        }

        @Override
        public void onStart(EntityIntelligent entity) {
            super.onStart(entity);
            entity.setDataProperty(ActorDataTypes.TARGET, entity.getMemoryStorage().get(memory).getId());
            entity.setDataFlag(ActorFlags.ANGRY);
            entity.level.addLevelSoundEvent(entity, SoundEvent.ANGRY, -1, Entity.HOGLIN, false, false);
        }

        @Override
        public void onStop(EntityIntelligent entity) {
            super.onStop(entity);
            entity.setDataFlag(ActorFlags.ANGRY, false);
            entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        }

        @Override
        public void onInterrupt(EntityIntelligent entity) {
            super.onInterrupt(entity);
            entity.setDataFlag(ActorFlags.ANGRY, false);
            entity.setDataProperty(ActorDataTypes.TARGET, 0L);
        }
    }
}
