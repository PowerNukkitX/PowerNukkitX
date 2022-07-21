package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBeehive;
import cn.nukkit.block.BlockFlower;
import cn.nukkit.blockentity.BlockEntityBeehive;
import cn.nukkit.entity.EntityFlyingAnimal;
import cn.nukkit.entity.ai.BehaviorGroup;
import cn.nukkit.entity.ai.IBehaviorGroup;
import cn.nukkit.entity.ai.behavior.Behavior;
import cn.nukkit.entity.ai.controller.SpaceMoveController;
import cn.nukkit.entity.ai.evaluator.PlayerEvaluator;
import cn.nukkit.entity.ai.executor.MoveToTargetExecutor;
import cn.nukkit.entity.ai.memory.NearestPlayerMemory;
import cn.nukkit.entity.ai.route.SimpleSpaceAStarRouteFinder;
import cn.nukkit.entity.ai.route.posevaluator.FlyingPosEvaluator;
import cn.nukkit.entity.ai.sensor.NearestPlayerSensor;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * @author joserobjr
 */
@Since("1.1.1.0-PN")
public class EntityBee extends EntityFlyingAnimal {

    @Since("1.1.1.0-PN")
    public static final int NETWORK_ID = 122;

    private int beehiveTimer = 600;

    private final IBehaviorGroup behaviorGroup = new BehaviorGroup(
            Set.of(
                    new Behavior(new MoveToTargetExecutor(NearestPlayerMemory.class), new PlayerEvaluator(), 1, 1)
            ),
            Set.of(new NearestPlayerSensor(50, 0)),
            Set.of(new SpaceMoveController()),
            new SimpleSpaceAStarRouteFinder(new FlyingPosEvaluator(), this)
    );

    @Since("1.1.1.0-PN")
    public EntityBee(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public IBehaviorGroup getBehaviorGroup() {
        return behaviorGroup;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
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

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public boolean getHasNectar() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void setHasNectar(boolean hasNectar) {

    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public boolean isAngry() {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void setAngry(boolean angry) {

    }

    @Override
    public boolean onUpdate(int currentTick) {
        super.onUpdate(currentTick);
        if (--beehiveTimer <= 0) {
            BlockEntityBeehive closestBeehive = null;
            double closestDistance = Double.MAX_VALUE;
            Optional<Block> flower = Arrays.stream(level.getCollisionBlocks(getBoundingBox().grow(4, 4, 4), false, true))
                    .filter(block -> block instanceof BlockFlower)
                    .findFirst();

            for (Block collisionBlock : level.getCollisionBlocks(getBoundingBox().grow(1.5, 1.5, 1.5))) {
                if (collisionBlock instanceof BlockBeehive) {
                    BlockEntityBeehive beehive = ((BlockBeehive) collisionBlock).getOrCreateBlockEntity();
                    double distance;
                    if (beehive.getOccupantsCount() < 4 && (distance = beehive.distanceSquared(this)) < closestDistance) {
                        closestBeehive = beehive;
                        closestDistance = distance;
                    }
                }
            }

            if (closestBeehive != null) {
                BlockEntityBeehive.Occupant occupant = closestBeehive.addOccupant(this);
                if (flower.isPresent()) {
                    occupant.setTicksLeftToStay(2400);
                    occupant.setHasNectar(true);
                }
            }
        }
        return true;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(10);
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void nectarDelivered(BlockEntityBeehive blockEntityBeehive) {

    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void leftBeehive(BlockEntityBeehive blockEntityBeehive) {

    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public void setAngry(Player player) {

    }


    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Bee";
    }
}
