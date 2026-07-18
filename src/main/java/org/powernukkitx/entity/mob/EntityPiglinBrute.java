package org.powernukkitx.entity.mob;

import org.powernukkitx.block.BlockDoor;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.EntityCheckEvaluator;
import org.powernukkitx.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import org.powernukkitx.entity.ai.evaluator.RandomSoundEvaluator;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.PiglinTransformExecutor;
import org.powernukkitx.entity.ai.executor.PlaySoundExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestEntitySensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerAngryPiglinSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.ai.sensor.NearestTargetEntitySensor;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author joserobjr
 * @since 2020-11-20
 */


public class EntityPiglinBrute extends EntityPiglin implements EntityWalkable {


    @Override
    @NotNull public String getIdentifier() {
        return PIGLIN_BRUTE;
    }

    public EntityPiglinBrute(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(50);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.35f);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PiglinTransformExecutor(), all(
                                entity -> entity.getLevel().getDimension() != Level.DIMENSION_NETHER,
                                entity -> !isImmobile(),
                                entity -> !entity.getNbt().getBoolean("IsImmuneToZombification")
                        ), 12, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_ANGRY, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> isAngry()), 10, 1),
                        new Behavior(new PlaySoundExecutor(Sound.MOB_PIGLIN_AMBIENT, 0.8f, 1.2f, 0.8f, 0.8f), all(new RandomSoundEvaluator(), entity -> !isAngry()), 9, 1),
                        new Behavior(new EntityPiglin.PiglinMeleeAttackExecutor(CoreMemoryTypes.ATTACK_TARGET, 0.5f, 40, true, 30),new EntityCheckEvaluator(CoreMemoryTypes.ATTACK_TARGET), 6, 1),
                        new Behavior(new EntityPiglin.PiglinMeleeAttackExecutor(CoreMemoryTypes.NEAREST_PLAYER, 0.5f, 40, false, 30), new EntityCheckEvaluator(CoreMemoryTypes.NEAREST_PLAYER), 5, 1),
                        new Behavior(new EntityPiglin.PiglinFleeFromTargetExecutor(CoreMemoryTypes.NEAREST_SHARED_ENTITY), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_SHARED_ENTITY),
                                entity -> !isBaby()
                        ), 3, 1),
                        new Behavior(new FlatRandomRoamExecutor(0.3f, 12, 100, false, -1, true, 10), none(), 1, 1)
                ),
                Set.of(new NearestPlayerSensor(40, 0, 20),
                        new NearestTargetEntitySensor<>(0, 16, 20,
                                List.of(CoreMemoryTypes.NEAREST_SUITABLE_ATTACK_TARGET), this::attackTarget),
                        new NearestPlayerAngryPiglinSensor(),
                        new NearestEntitySensor(EntityZombiePigman.class, CoreMemoryTypes.NEAREST_SHARED_ENTITY, 8 , 0),
                        new BlockSensor(BlockDoor.class, CoreMemoryTypes.NEAREST_BLOCK, 2, 2, 20)
                ),
                Set.of(new WalkController(), new LookController(true, true)),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.diffHandDamage = new float[]{6f, 10f, 15f};
        setItemInHand(Item.get(Item.GOLDEN_AXE));
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case Entity.WITHER_SKELETON, Entity.WITHER -> true;
            default -> false;
        };
    }

    @Override
    public String getOriginalName() {
        return "Piglin Brute";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("piglin", "adult_piglin", "piglin_brute", "monster");
    }
}
