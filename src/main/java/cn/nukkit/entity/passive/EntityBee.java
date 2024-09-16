package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.entity.EntityFlyable;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.behaviorgroup.BehaviorGroup;
import cn.nukkit.entity.ai.behaviorgroup.IBehaviorGroup;
import cn.nukkit.entity.ai.controller.FluctuateController;
import cn.nukkit.entity.ai.controller.LiftController;
import cn.nukkit.entity.ai.controller.LookController;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.controller.WalkController;
import cn.nukkit.entity.ai.evaluator.MemoryCheckNotEmptyEvaluator;
import cn.nukkit.entity.ai.evaluator.PassByTimeEvaluator;
import cn.nukkit.entity.ai.evaluator.ProbabilityEvaluator;
import cn.nukkit.entity.ai.executor.EntityBreedingExecutor;
import cn.nukkit.entity.ai.executor.FlatRandomRoamExecutor;
import cn.nukkit.entity.ai.executor.InLoveExecutor;
import cn.nukkit.entity.ai.executor.LookAtTargetExecutor;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.executor.SpaceRandomRoamExecutor;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.entity.ai.route.finder.impl.SimpleFlatAStarRouteFinder;
import cn.nukkit.entity.ai.route.finder.impl.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.route.posevaluator.WalkingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestFeedingPlayerSensor;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author joserobjr
 */

public class EntityBee extends EntityAnimal implements EntityFlyable {

    @Override
    @NotNull public String getIdentifier() {
        return BEE;
    }

    private final int beehiveTimer = 600;


    public EntityBee(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup requireBehaviorGroup() {
        return new BehaviorGroup(
                this.tickSpread,
                Set.of(),
                Set.of(
                        new Behavior(new SpaceRandomRoamExecutor(0.15f, 12, 100, 20, false, -1, true, 10), (entity -> true), 1, 1)
                ),
                Set.of(),
                Set.of(new SpaceMoveController(), new LookController(true, true), new LiftController()),
                new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this),
                this
        );
    }

    @Override
    public float getWidth() {
        if (this.isBaby()) {
            return 0.275f;
        }
        return 0.55f;
    }

    @Override
    public float getHeight() {
        if (this.isBaby()) {
            return 0.25f;
        }
        return 0.5f;
    }

    public boolean getHasNectar() {
        return false;
    }

    public void setHasNectar(boolean hasNectar) {

    }

    public boolean isAngry() {
        return false;
    }

    public void setAngry(boolean angry) {

    }

    public void setAngry(Player player) {

    }

    @Override
    public boolean onUpdate(int currentTick) {
        return super.onUpdate(currentTick);
        //todo: 属于实体AI范畴，应迁移到实体AI部分实现，此方法开销巨大
//        if (--beehiveTimer <= 0) {
//            BlockEntityBeehive closestBeehive = null;
//            double closestDistance = Double.MAX_VALUE;
//            Optional<Block> flower = Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(4, 4, 4), false, true))
//                    .filter(block -> block instanceof BlockFlower)
//                    .findFirst();
//
//            for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5))) {
//                if (collisionBlock instanceof BlockBeehive) {
//                    BlockEntityBeehive beehive = ((BlockBeehive) collisionBlock).getOrCreateBlockEntity();
//                    double distance;
//                    if (beehive.getOccupantsCount() < 4 && (distance = beehive.distanceSquared(this)) < closestDistance) {
//                        closestBeehive = beehive;
//                        closestDistance = distance;
//                    }
//                }
//            }
//
//            if (closestBeehive != null) {
//                BlockEntityBeehive.Occupant occupant = closestBeehive.addOccupant(this);
//                if (flower.isPresent()) {
//                    occupant.setTicksLeftToStay(2400);
//                    occupant.setHasNectar(true);
//                }
//            }
//        }
//        return true;
    }

    @Override
    protected void initEntity() {
        this.setMaxHealth(10);
        super.initEntity();
    }

    public void nectarDelivered(BlockEntityBeehive blockEntityBeehive) {

    }

    public void leftBeehive(BlockEntityBeehive blockEntityBeehive) {

    }

    @Override
    public String getOriginalName() {
        return "Bee";
    }
}
