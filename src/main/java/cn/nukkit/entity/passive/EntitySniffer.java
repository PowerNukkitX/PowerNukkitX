package cn.nukkit.entity.passive;

import cn.nukkit.entity.EntityID;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.AnimalGrowExecutor;
import cn.nukkit.entity.ai.executor.BreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.LoveTimeoutExecutor;
import cn.nukkit.entity.ai.executor.TemptExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.components.AgeableComponent;
import cn.nukkit.entity.components.BreedableComponent;
import cn.nukkit.entity.components.HealableComponent;
import cn.nukkit.entity.components.HealthComponent;
import cn.nukkit.entity.components.MovementComponent;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;


// TODO: Pregnant logic / Behavior
public class EntitySniffer extends EntityAnimal {

    public EntitySniffer(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public @NotNull String getIdentifier() {
        return SNIFFER;
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.855F;
        }
        return 1.9f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.7875F;
        }
        return 1.75f;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(14);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.09f);
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("sniffer", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                null,
                null,
                null,
                null,
                Set.of(
                    ItemID.TORCHFLOWER_SEEDS
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.SNIFFER, EntityID.SNIFFER)
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
    public HealableComponent getComponentHealable() {
        return new HealableComponent(
                List.of(
                    new HealableComponent.Item(ItemID.TORCHFLOWER_SEEDS, 2)
                )
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                null,
                2400f,
                List.of(
                    new AgeableComponent.FeedItem(ItemID.TORCHFLOWER_SEEDS)
                ),
                null,
                null,
                null
        );
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
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
                        new FlatRandomRoamExecutor(0.4f, 12, 40, true, 100, true, 10),
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
                    new FluctuateController()
                ),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

}
