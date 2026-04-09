package cn.nukkit.entity.passive;

import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.EntitySwimmable;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.DiveController;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import org.cloudburstmc.nbt.NbtMap;
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
    

    public EntityTurtle(IChunk chunk, NbtMap nbt) {
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
                        new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10),
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
                        new FlatRandomRoamExecutor(0.2f, 12, 100, false, -1, true, 10),
                            (entity -> true),
                        1, 1
                    )
                ),
                Set.of(
                    new NearestPlayerSensor(8, 0, 20)
                ),
                Set.of(
                    new WalkController(),
                    new LookController(true, true),
                    new FluctuateController(), new SpaceMoveController(),
                    new DiveController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
