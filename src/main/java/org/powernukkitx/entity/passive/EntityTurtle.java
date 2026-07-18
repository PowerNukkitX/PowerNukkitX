package org.powernukkitx.entity.passive;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntitySwimmable;
import org.powernukkitx.entity.EntityWalkable;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.ConditionalController;
import org.powernukkitx.entity.ai.controller.DiveController;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.SpaceMoveController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.SpaceRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.executor.TurtleMoveToWaterExecutor;
import org.powernukkitx.entity.ai.memory.CoreMemoryTypes;
import org.powernukkitx.entity.ai.route.finder.impl.ConditionalAStarRouteFinder;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import org.powernukkitx.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import org.powernukkitx.entity.ai.route.posevaluator.SwimmingPosEvaluator;
import org.powernukkitx.entity.ai.route.posevaluator.WalkingPosEvaluator;
import org.powernukkitx.entity.ai.sensor.BlockSensor;
import org.powernukkitx.entity.ai.sensor.NearestPlayerSensor;
import org.powernukkitx.entity.components.AgeableComponent;
import org.powernukkitx.entity.components.BreedableComponent;
import org.powernukkitx.entity.components.HealthComponent;
import org.powernukkitx.entity.components.MovementComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * @author PetteriM1
 */
// TODO: Pregnant logic, home logic
public class EntityTurtle extends EntityAnimal implements EntitySwimmable, EntityWalkable {
    @Override
    @NotNull public String getIdentifier() {
        return TURTLE;
    }
    

    public EntityTurtle(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getOriginalName() {
        return "Turtle";
    }

    @Override
    public Set<String> typeFamily() {
        if (this.isBaby()) {
            return Set.of("turtle", "baby_turtle", "mob");
        }
        return Set.of("turtle", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    BlockID.SEAGRASS
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.TURTLE, EntityID.TURTLE)
                ),
                true,
                null,
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
                    new AgeableComponent.FeedItem(BlockID.SEAGRASS)
                ),
                null,
                null,
                Set.of(
                    ItemID.TURTLE_SCUTE
                )
        );
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.6f;
        }
        return 1.2f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.2f;
        }
        return 0.4f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(30);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.1f);
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(source.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION && getLevelBlock().canPassThrough()) {
            return false;
        }
        return super.attack(source);
    }

    public void setBreedingAge(int ticks) {

    }

    public void setHomePos(Vector3 pos) {

    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        BlockID.SEAGRASS
    );

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
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
                        new TurtleMoveToWaterExecutor(CoreMemoryTypes.NEAREST_BLOCK, 0.1f),
                            entity -> !entity.isInsideOfWater() && entity.getMemoryStorage().notEmpty(CoreMemoryTypes.NEAREST_BLOCK),
                        5, 1
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.12f, 12, 40, true, 100, true, 10),
                            new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 100),
                        4, 1
                    ),
                    new Behavior(
                        new TemptExecutor(1.1f, true, false, false, 10, 2.0f, null, TEMPT_ITEMS),
                            all(
                                e -> !e.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE),
                                e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                            ),
                        3, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            new ProbabilityEvaluator(4, 10),
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.1f, 12, 100, false, -1, true, 10),
                            entity -> !entity.isInsideOfWater(),
                        1, 1
                    ),
                    new Behavior(
                        new SpaceRandomRoamExecutor(0.12f, 30, 15, 80, false, 160, false, 10),
                            entity -> entity.isInsideOfWater(),
                        1, 1
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20),
                    new BlockSensor(BlockFlowingWater.class, CoreMemoryTypes.NEAREST_BLOCK, 16, 5, 10)
                ),
                Set.of(
                    new LookController(true, true),
                    new ConditionalController(
                        Pair.of(entity -> entity.isInsideOfWater(), new DiveController()),
                        Pair.of(entity -> entity.isInsideOfWater(), new SpaceMoveController()),
                        Pair.of(entity -> !entity.isInsideOfWater(), new WalkController()),
                        Pair.of(entity -> !entity.isInsideOfWater(), new FluctuateController())
                    )
                ),
                new ConditionalAStarRouteFinder(
                    this,
                    Pair.of(entity -> !entity.isInsideOfWater(), new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this)),
                    Pair.of(entity -> entity.isInsideOfWater(), new SimpleSpaceAStarRouteFinder(new SwimmingPosEvaluator(), this))
                ),
                this
        );
    }
}
