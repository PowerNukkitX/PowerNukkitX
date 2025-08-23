package cn.nukkit.entity.mob;

import cn.nukkit.block.BlockDoor;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.EntityCheckEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.RandomSoundEvaluator;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.PiglinTransformExecutor;
import cn.nukkit.entity.ai.executor.PlaySoundExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.BlockSensor;
import cn.nukkit.entity.ai.sensor.NearestEntitySensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerAngryPiglinSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestTargetEntitySensor;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

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
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new PiglinTransformExecutor(), all(
                                entity -> entity.getLevel().getDimension() != Level.DIMENSION_NETHER,
                                entity -> !isImmobile(),
                                entity -> !entity.namedTag.getBoolean("IsImmuneToZombification")
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
        this.setMaxHealth(50);
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
