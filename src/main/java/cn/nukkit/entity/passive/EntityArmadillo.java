package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.AnyMatchEvaluator;
import cn.nukkit.entity.ai.evaluator.IBehaviorEvaluator;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.armadillo.PeekExecutor;
import cn.nukkit.entity.ai.executor.armadillo.RelaxingExecutor;
import cn.nukkit.entity.ai.executor.armadillo.RollUpExecutor;
import cn.nukkit.entity.ai.executor.armadillo.ShedExecutor;
import cn.nukkit.entity.ai.executor.armadillo.UnrollingExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.data.property.EntityProperty;
import cn.nukkit.entity.data.property.EnumEntityProperty;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBrush;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
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
    public Set<String> typeFamily() {
        return Set.of("armadillo", "mob");
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(
                        //用于刷新InLove状态的核心行为
                        new Behavior(
                                new InLoveExecutor(400),
                                all(
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_BE_FEED_TIME, 0, 400),
                                        new PassByTimeEvaluator(CoreMemoryTypes.LAST_IN_LOVE_TIME, 6000, Integer.MAX_VALUE)
                                ),
                                1, 1, 1, false
                        )
                ),
                Set.of(
                        new Behavior(new UnrollingExecutor(), entity -> getRollState() == RollState.ROLLED_UP_UNROLLING, 8, 1),
                        new Behavior(new PeekExecutor(), any(
                                entity -> getRollState() == RollState.ROLLED_UP_PEEKING,
                                all(
                                        entity -> getRollState() == RollState.ROLLED_UP_RELAXING,
                                        new ProbabilityEvaluator(1,0xFFF)
                                )
                        ), 7, 1),
                        new Behavior(new RollUpExecutor(), new RollupEvaluator(), 6, 1),
                        new Behavior(new RelaxingExecutor(), new ProbabilityEvaluator(1,0xFFFF), 5, 1),
                        new Behavior(new ShedExecutor(), all(
                                entity -> getRollState() == RollState.UNROLLED,
                                new PassByTimeEvaluator(CoreMemoryTypes.NEXT_SHED, 0)
                        ), 5, 1),
                        new Behavior(new EntityBreedingExecutor<>(EntityArmadillo.class, 16, 100, 0.5f), entity -> entity.getMemoryStorage().get(CoreMemoryTypes.IS_IN_LOVE), 7, 1),
                        new Behavior(new MoveToTargetExecutor(CoreMemoryTypes.NEAREST_FEEDING_PLAYER, 0.4f, true), all(
                                new MemoryCheckNotEmptyEvaluator(CoreMemoryTypes.NEAREST_FEEDING_PLAYER),
                                entity -> getRollState() == RollState.UNROLLED
                        ), 2, 1),
                        new Behavior(new LookAtTargetExecutor(CoreMemoryTypes.NEAREST_PLAYER, 100), all(
                                new ProbabilityEvaluator(4, 10),
                                entity -> getRollState() == RollState.UNROLLED
                        ), 1, 1, 100),
                        new Behavior(new FlatRandomRoamExecutor(0.2f, 12, 20, false, -1, true, 40), entity -> getRollState() == RollState.UNROLLED, 1, 1)
                ),
                Set.of(new NearestFeedingPlayerSensor(8, 0), new NearestPlayerSensor(8, 0, 20)),
                Set.of(new WalkController(), new LookController(true, true), new FluctuateController()),
                new SimpleFlatAStarRouteFinder(new WalkingPosEvaluator(), this),
                this
        );
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if(player.getInventory().getUnclonedItem(player.getInventory().getHeldItemIndex()) instanceof ItemBrush brush) {
            getLevel().dropItem(this, Item.get(Item.ARMADILLO_SCUTE));
            getLevel().addSound(player, Sound.MOB_ARMADILLO_BRUSH);
            brush.incDamage(16);
            if(brush.getDamage() >= brush.getMaxDurability()) {
                player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                player.getInventory().clear(player.getInventory().getHeldItemIndex());
            }
        }
        return super.onInteract(player, item, clickedPos);
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(12);
        super.initEntity();
        setMovementSpeed(0.5f);
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

    @Override
    public boolean isBreedingItem(Item item) {
        return Objects.equals(item.getId(), Item.SPIDER_EYE);
    }

    public void setRollState(RollState state) {
        this.rollState = state;
        setEnumEntityProperty(PROPERTY_STATE, state.getState());
        setDataFlag(EntityFlag.BODY_ROTATION_BLOCKED, rollState != RollState.UNROLLED);
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
