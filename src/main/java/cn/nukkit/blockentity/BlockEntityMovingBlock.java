package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockSlime;
import cn.nukkit.block.BlockState;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.Registries;
import cn.nukkit.utils.HashUtils;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 * @since 11.4.2017
 */
@Slf4j
public class BlockEntityMovingBlock extends BlockEntitySpawnable {
    private static final double SLIME_PUSH_MOTION = 0.75;

    protected Block block;
    protected BlockVector3 piston;
    //true if the piston is extending instead of withdrawing.
    protected boolean expanding;

    public BlockEntityMovingBlock(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * {
     * expanding: 0b,
     * id: MovingBlock,
     * isMovable: 1b,
     * movingBlock: {
     * name: "minecraft:air",
     * states: {
     * },
     * version: 18100737
     * },
     * movingBlockExtra: {
     * name: "minecraft:air",
     * states: {
     * },
     * version: 18100737
     * },
     * pistonPosX: 0,
     * pistonPosY: -1,
     * pistonPosZ: 0,
     * x: 0,
     * y: 100,
     * z: 0
     * }
     */
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (nbt.contains("movingBlock")) {
            CompoundTag movingBlock = nbt.getCompound("movingBlock");
            int blockhash = HashUtils.fnv1a_32_nbt_palette(movingBlock.toNetwork());
            BlockState blockState = Registries.BLOCKSTATE.get(blockhash);
            if(blockState==null){
                log.error("Can't load moving block {}",movingBlock.toSNBT());
            }else{
                this.block = blockState.toBlock();
            }
            this.block.x = x;
            this.block.y = y;
            this.block.z = z;
        } else {
            this.close();
        }

        if (nbt.contains("pistonPosX") && nbt.contains("pistonPosY") && nbt.contains("pistonPosZ")) {
            this.piston = new BlockVector3(nbt.getInt("pistonPosX"), nbt.getInt("pistonPosY"), nbt.getInt("pistonPosZ"));
        } else {
            this.piston = new BlockVector3(0, -1, 0);
        }
        this.expanding = nbt.getBoolean("expanding");
    }

    public @Nullable CompoundTag getMovingBlockEntityCompound() {
        if (this.nbt.contains("movingEntity")) {
            return this.nbt.getCompound("movingEntity");
        }

        return null;
    }

    public Block getMovingBlock() {
        return this.block;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag tag = super.getSpawnCompound()
                .putBoolean("expanding", this.expanding)
                .putInt("pistonPosX", this.piston.x)
                .putInt("pistonPosY", this.piston.y)
                .putInt("pistonPosZ", this.piston.z)
                .putBoolean("isMovable", this.movable);

        if (this.nbt.contains("movingBlock")) {
            tag.putCompound("movingBlock", this.nbt.getCompound("movingBlock").copy());
        }
        if (this.nbt.contains("movingBlockExtra")) {
            tag.putCompound("movingBlockExtra", this.nbt.getCompound("movingBlockExtra").copy());
        }
        if (this.nbt.contains("movingEntity")) {
            tag.putCompound("movingEntity", this.nbt.getCompound("movingEntity").copy());
        }

        return tag;
    }

    public void moveCollidedEntities(BlockEntityPistonArm piston, BlockFace moveDirection) {
        var bb = block.getBoundingBox();
        if (bb == null)
            return;
        bb = bb.getOffsetBoundingBox(
                getMovementOffset(piston, moveDirection.getXOffset(), piston.lastProgress),
                getMovementOffset(piston, moveDirection.getYOffset(), piston.lastProgress),
                getMovementOffset(piston, moveDirection.getZOffset(), piston.lastProgress)
                //Move the entity standing on the moving block
        ).addCoord(
                getMovementDelta(piston, moveDirection.getXOffset()),
                getMovementDelta(piston, moveDirection.getYOffset()),
                getMovementDelta(piston, moveDirection.getZOffset())
        ).addCoord(0, moveDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);
        for (Entity entity : this.level.getCollidingEntities(bb)) {
            boolean moved = piston.moveEntity(entity, moveDirection);
            boolean affected = moved || piston.markEntityAffected(entity);
            //Prevent entity from falling through the block
            if (!(moveDirection.getAxis() == BlockFace.Axis.Y || entity.closed || !entity.canBePushedByPiston())) {
                double feetY = entity.getBoundingBox().getMinY();
                double blockTop = block.getBoundingBox().getMaxY();
                if (!(feetY < blockTop - 0.125 || feetY > blockTop + 0.5)) {
                    double lift = blockTop - feetY;
                    if (lift > 0) {
                        entity.move(0, lift, 0);
                    }
                    if (entity.motionY < 0) {
                        entity.motionY = 0;
                    }
                    entity.onGround = true;
                    entity.resetFallDistance();
                }
            }
            //Add motion if pushed by slime block
            if (affected && piston.extending && block instanceof BlockSlime && !entity.closed && entity.canBePushedByPiston()) {
                Vector3 motion = entity.getMotion();
                switch (moveDirection.getAxis()) {
                    case X -> motion.x = getSlimePushMotion(motion.x, moveDirection.getXOffset());
                    case Y -> motion.y = getSlimePushMotion(motion.y, moveDirection.getYOffset());
                    case Z -> motion.z = getSlimePushMotion(motion.z, moveDirection.getZOffset());
                }
                entity.setMotion(motion);
            }
        }
    }

    private double getSlimePushMotion(double currentMotion, int axisOffset) {
        double pushMotion = axisOffset * SLIME_PUSH_MOTION;
        if (axisOffset > 0) {
            return Math.max(currentMotion, pushMotion);
        }
        if (axisOffset < 0) {
            return Math.min(currentMotion, pushMotion);
        }
        return currentMotion;
    }

    private double getMovementOffset(BlockEntityPistonArm piston, int axisOffset, float progress) {
        if (piston.extending) {
            return (progress - 1) * axisOffset;
        }
        return -progress * axisOffset;
    }

    private double getMovementDelta(BlockEntityPistonArm piston, int axisOffset) {
        if (piston.extending) {
            return (piston.progress - piston.lastProgress) * axisOffset;
        }
        return (piston.lastProgress - piston.progress) * axisOffset;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == BlockID.MOVING_BLOCK;
    }
}
