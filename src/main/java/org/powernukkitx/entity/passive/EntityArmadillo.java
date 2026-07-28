package org.powernukkitx.entity.passive;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityFilter;
import org.powernukkitx.entity.EntityID;
import org.powernukkitx.entity.EntityIntelligent;
import org.powernukkitx.entity.ai.behavior.Behavior;
import org.powernukkitx.entity.ai.behaviorgroup.BehaviorGroup;
import org.powernukkitx.entity.ai.behaviorgroup.IBehaviorGroup;
import org.powernukkitx.entity.ai.controller.FluctuateController;
import org.powernukkitx.entity.ai.controller.LookController;
import org.powernukkitx.entity.ai.controller.WalkController;
import org.powernukkitx.entity.ai.evaluator.AnyMatchEvaluator;
import org.powernukkitx.entity.ai.evaluator.IBehaviorEvaluator;
import org.powernukkitx.entity.ai.evaluator.PassByTimeEvaluator;
import org.powernukkitx.entity.ai.evaluator.ProbabilityEvaluator;
import org.powernukkitx.entity.ai.executor.AnimalGrowExecutor;
import org.powernukkitx.entity.ai.executor.BreedingExecutor;
import org.powernukkitx.entity.ai.executor.FlatRandomRoamExecutor;
import org.powernukkitx.entity.ai.executor.LookAtTargetExecutor;
import org.powernukkitx.entity.ai.executor.LoveTimeoutExecutor;
import org.powernukkitx.entity.ai.executor.TemptExecutor;
import org.powernukkitx.entity.ai.executor.armadillo.PeekExecutor;
import org.powernukkitx.entity.ai.executor.armadillo.RelaxingExecutor;
import org.powernukkitx.entity.ai.executor.armadillo.RollUpExecutor;
import org.powernukkitx.entity.ai.executor.armadillo.ShedExecutor;
import org.powernukkitx.entity.ai.executor.armadillo.UnrollingExecutor;
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
import org.powernukkitx.entity.mob.EntityMob;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBrush;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.Utils;
import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EntityArmadillo extends EntityAnimal {
    public static final EntityProperty[] PROPERTIES = new EntityProperty[]{
        new EnumEntityProperty("minecraft:armadillo_state", new String[]{
            "unrolled",
            "rolled_up",
            "rolled_up_peeking",
            "rolled_up_relaxing",
            "rolled_up_unrolling"
        }, "unrolled", true)
    };
    private final static String PROPERTY_STATE = "minecraft:armadillo_state";

    public EntityArmadillo(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Getter
    private RollState rollState = RollState.UNROLLED;

    @Override
    public @NotNull String getIdentifier() {
        return ARMADILLO;
    }

    @Override
    public HealthComponent getComponentHealth() {
        return HealthComponent.value(12);
    }

    @Override
    protected @Nullable MovementComponent getComponentMovement() {
        return MovementComponent.value(0.14f);
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("armadillo", "mob");
    }

    @Override
    public @Nullable BreedableComponent getComponentBreedable() {
        return new BreedableComponent(
                EntityFilter.all(
                    (self, ctx) -> self instanceof EntityArmadillo armadillo && armadillo.getRollState() == EntityArmadillo.RollState.UNROLLED
                ),
                Set.of(
                    ItemID.SPIDER_EYE
                ),
                List.of(
                    new BreedableComponent.BreedsWith(EntityID.ARMADILLO, EntityID.ARMADILLO)
                ),
                false
        );
    }

    @Override
    public AgeableComponent getComponentAgeable() {
        return new AgeableComponent(
                EntityFilter.all(
                    (self, ctx) -> self instanceof EntityArmadillo armadillo && armadillo.getRollState() == EntityArmadillo.RollState.UNROLLED
                ),
                1200f,
                List.of(
                    new AgeableComponent.FeedItem(ItemID.SPIDER_EYE)
                ),
                null,
                null,
                null
        );
    }

    private static final Set<String> TEMPT_ITEMS = Set.of(
        ItemID.SPIDER_EYE
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
                        new UnrollingExecutor(), 
                            entity -> getRollState() == RollState.ROLLED_UP_UNROLLING,
                        7, 1
                    ),
                    new Behavior(
                        new PeekExecutor(),
                            any(
                                entity -> getRollState() == RollState.ROLLED_UP_PEEKING,
                                all(
                                    entity -> getRollState() == RollState.ROLLED_UP_RELAXING,
                                    new ProbabilityEvaluator(1,0xFFF)
                                )
                            ),
                        6, 1
                    ),
                    new Behavior(
                        new RollUpExecutor(), new RollupEvaluator(),
                        5, 1
                    ),
                    new Behavior(
                        new RelaxingExecutor(), new ProbabilityEvaluator(1,0xFFFF),
                        4, 1
                    ),
                    new Behavior(
                        new ShedExecutor(),
                            all(
                                entity -> getRollState() == RollState.UNROLLED,
                                new PassByTimeEvaluator(CoreMemoryTypes.NEXT_SHED, 0)
                            ),
                        4, 1
                    ),
                    new Behavior(
                        new BreedingExecutor(16, 200, 0.35f),
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
                                e -> this.getRollState() == RollState.UNROLLED,
                                e -> TemptExecutor.hasTemptingPlayer(e, false, 10, TEMPT_ITEMS)
                            ),
                        2, 1
                    ),
                    new Behavior(
                        new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100),
                            all(
                                new ProbabilityEvaluator(4, 10),
                                entity -> getRollState() == RollState.UNROLLED
                            ),
                        1, 1, 100
                    ),
                    new Behavior(
                        new FlatRandomRoamExecutor(0.2f, 12, 20, false, -1, true, 40),
                            entity -> getRollState() == RollState.UNROLLED,
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

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (superResult) return true;

        if(player.getInventory().getUnclonedItem(player.getInventory().getHeldItemIndex()) instanceof ItemBrush brush) {
            getLevel().dropItem(this, Item.get(Item.ARMADILLO_SCUTE));
            getLevel().addSound(player, Sound.MOB_ARMADILLO_BRUSH);
            brush.incDamage(16);
            if(brush.getDamage() >= brush.getMaxDurability()) {
                player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                player.getInventory().clear(player.getInventory().getHeldItemIndex());
            }
        }

        return superResult;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setRollState(RollState.UNROLLED);
        getMemoryStorage().put(CoreMemoryTypes.NEXT_SHED, getLevel().getTick() + Utils.rand(6_000, 10_800));
    }

    @Override
    public float getWidth() {
        if (isBaby()) {
            return 0.42F;
        }
        return 0.7f;
    }

    @Override
    public float getHeight() {
        if (isBaby()) {
            return 0.39f;
        }
        return 0.65F;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if(getRollState() != RollState.UNROLLED) {
            source.setDamage((source.getDamage()-1)/2f);
        }
        return super.attack(source);
    }

    public void setRollState(RollState state) {
        this.rollState = state;
        setEnumEntityProperty(PROPERTY_STATE, state.getState());
        setDataFlag(ActorFlags.BODY_ROTATION_BLOCKED, rollState != RollState.UNROLLED);
    }


    public enum RollState {

        UNROLLED("unrolled"),
        ROLLED_UP("rolled_up"),
        ROLLED_UP_PEEKING("rolled_up_peeking"),
        ROLLED_UP_RELAXING("rolled_up_relaxing"),
        ROLLED_UP_UNROLLING("rolled_up_unrolling");

        @Getter
        private final String state;
        RollState(String s) {
            state = s;
        }
    }

    public static class RollupEvaluator implements IBehaviorEvaluator {

        @Override
        public boolean evaluate(EntityIntelligent entity) {
            return new AnyMatchEvaluator(
                    new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_ATTACKED_TIME, 0, 1),
                    entity1 -> {
                        for(Entity other : entity.getLevel().getCollidingEntities(entity.getBoundingBox().grow(7, 2, 7))) {
                            if(other instanceof EntityMob mob) {
                                if(mob.isUndead()) return true;
                            } else if(other instanceof Player player) {
                                if(player.isSprinting()) return true;
                            }
                        }
                        return false;
                    }
            ).evaluate(entity);
        }
    }
}
