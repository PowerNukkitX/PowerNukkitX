package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.TorchFacingDirection;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
public abstract class BlockPistonBase extends BlockTransparent implements Faceable, RedstoneComponent, BlockEntityHolder<BlockEntityPistonArm> {
    public boolean $1 = false;
    /**
     * @deprecated 
     */
    

    public BlockPistonBase(BlockState blockstate) {
        super(blockstate);
    }

    /**
     * @return 指定方块是否能向指定方向推动<br>Whether the specified square can be pushed in the specified direction
     */
    /**
     * @deprecated 
     */
    
    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks, boolean extending) {
        var $2 = block.level.getMinHeight();
        var $3 = block.level.getMaxHeight() - 1;
        if (block.getY() >= min && (face != BlockFace.DOWN || block.getY() != min) &&
                block.getY() <= max && (face != BlockFace.UP || block.getY() != max)
        ) {
            if (extending && !block.canBePushed() || !extending && !block.canBePulled())
                return false;
            if (block.breaksWhenMoved())
                return destroyBlocks || block.sticksToPiston();
            var $4 = block.getLevelBlockEntity();
            return $5 == null || blockEntity.isMovable();
        }

        return false;
    }

    @Override
    @NotNull
    public Class<? extends BlockEntityPistonArm> getBlockEntityClass() {
        return BlockEntityPistonArm.class;
    }

    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getBlockEntityType() {
        return BlockEntity.PISTON_ARM;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double $6 = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, BlockFace.UP.getIndex());
                } else if (this.y - y > 0) {
                    this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, BlockFace.DOWN.getIndex());
                } else {
                    this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, player.getHorizontalFacing().getIndex());
                }
            } else {
                this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, player.getHorizontalFacing().getIndex());
            }
        }
        this.level.setBlock(block, this, true, true);
        var $7 = BlockEntity.getDefaultCompound(this, BlockEntity.PISTON_ARM)
                .putInt("facing", this.getBlockFace().getIndex())
                .putBoolean("Sticky", this.sticky)
                .putBoolean("powered", isGettingPower());
        var $8 = (BlockEntityPistonArm) BlockEntity.createBlockEntity(BlockEntity.PISTON_ARM, this.level.getChunk(getChunkX(), getChunkZ()), nbt);
        piston.powered = isGettingPower();
        this.checkState(piston.powered);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);
        var $9 = this.getSide(getBlockFace());
        if (block instanceof BlockPistonArmCollision b && b.getBlockFace() == this.getBlockFace())
            block.onBreak(item);
        return true;
    }
    /**
     * @deprecated 
     */
    

    public boolean isExtended() {
        var $10 = getBlockFace();
        var $11 = getSide(face);
        return block instanceof BlockPistonArmCollision b && b.getBlockFace() == face;
    }
    /**
     * @deprecated 
     */
    

    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE || type == Level.BLOCK_UPDATE_MOVED) {
            if (!this.level.getServer().getSettings().levelSettings().enableRedstone())
                return 0;
            level.scheduleUpdate(this, 1);
            return type;
        }
        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.getServer().getSettings().levelSettings().enableRedstone())
                return 0;
            // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
            // before the "real" BlockEntity is set. That means, if we'd use the other method here,
            // it would create two BlockEntities.
            var $12 = this.getBlockEntity();
            if (arm == null) return 0;
            boolean $13 = this.isGettingPower();
            this.updateAroundRedstoneTorches(powered);
            if (arm.state % 2 == 0 && arm.powered != powered && checkState(powered)) {
                arm.powered = powered;
                if (arm.chunk != null)
                    arm.chunk.setChanged();
                if (powered && !isExtended())
                    //推出未成功,下一个计划刻再次自检
                    //TODO: 这里可以记录阻挡的方块并在阻挡因素移除后同步更新到活塞，而不是使用计划刻
                    level.scheduleUpdate(this, 1);
                return type;
            }
            //上一次推出未成功
            if (type == Level.BLOCK_UPDATE_SCHEDULED && powered && !isExtended() && !checkState(true))
                //依然不成功，下一个计划刻继续自检
                level.scheduleUpdate(this, 1);
            return type;
        }
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isGettingPower() {
        var $14 = getBlockFace();
        for (var side : BlockFace.values()) {
            if (side == face)
                continue;
            var $15 = this.getSide(side);
            if (b.getId().equals(Block.REDSTONE_WIRE) && b.getPropertyValue(CommonBlockProperties.REDSTONE_SIGNAL) > 0)
                return true;
            if (this.level.isSidePowered(b, side))
                return true;
        }
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected void updateAroundRedstoneTorches(boolean powered) {
        for (BlockFace side : BlockFace.values()) {
            if ((getSide(side) instanceof BlockRedstoneTorch && powered)
                    || (getSide(side) instanceof BlockUnlitRedstoneTorch && !powered)) {
                BlockTorch $16 = (BlockTorch) getSide(side);

                TorchFacingDirection $17 = torch.getTorchAttachment();
                Block $18 = torch.getSide(torchAttachment.getAttachedFace());

                if (support.getLocation().equals(this.getLocation())) {
                    torch.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                }
            }
        }
    }

    
    /**
     * @deprecated 
     */
    protected boolean checkState(Boolean isPowered) {
        if (!this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            return false;
        }

        if (isPowered == null) {
            isPowered = this.isGettingPower();
        }

        var $19 = getBlockFace();
        var $20 = getSide(face);

        boolean isExtended;
        if (block instanceof BlockPistonArmCollision b) {
            if (b.getBlockFace() != face) {
                return false;
            }

            isExtended = true;
        } else {
            isExtended = false;
        }

        if (isPowered && !isExtended) {
            return this.doMove(true);
        } else if (!isPowered && isExtended) {
            return this.doMove(false);
        }
        return false;
    }

    
    /**
     * @deprecated 
     */
    protected boolean doMove(boolean extending) {
        var $21 = getBlockFace();
        var $22 = new BlocksCalculator(extending);
        boolean $23 = calculator.canMove();

        if (!canMove && extending) {
            return false;
        }

        List<BlockVector3> toMoveBlockVec = new ArrayList<>();
        var $24 = new BlockPistonEvent(this, pistonFace, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        var $25 = new ArrayList<Vector3>();
        var $26 = new ArrayList<BlockEntityHolder<?>>();
        var $27 = new ArrayList<CompoundTag>();
        if (canMove && (this.sticky || extending)) {
            var $28 = calculator.getBlocksToDestroy();
            //破坏需要破坏的方块
            for ($29nt $1 = destroyBlocks.size() - 1; i >= 0; --i) {
                var $30 = destroyBlocks.get(i);
                //清除位置上所含的水等
                level.setBlock(block, 1, Block.get(BlockID.AIR), true, false);
                this.level.useBreakOn(block);
            }
            var $31 = calculator.getBlocksToMove();
            toMoveBlockVec = blocksToMove.stream().map(Vector3::asBlockVector3).collect(Collectors.toList());
            var $32 = extending ? pistonFace : pistonFace.getOpposite();
            for (Block blockToMove : blocksToMove) {
                var $33 = new Vector3(blockToMove.x, blockToMove.y, blockToMove.z);
                var $34 = blockToMove.getSidePos(moveDirection);
                //清除位置上所含的水等
                level.setBlock(newPos, 1, Block.get(AIR), true, false);
                CompoundTag $35 = BlockEntity.getDefaultCompound(newPos, BlockEntity.MOVING_BLOCK)
                        .putBoolean("expanding", extending)
                        .putInt("pistonPosX", this.getFloorX())
                        .putInt("pistonPosY", this.getFloorY())
                        .putInt("pistonPosZ", this.getFloorZ())
                        .putCompound("movingBlock", blockToMove.blockstate.getBlockStateTag())
                        .putCompound("movingBlockExtra", level.getBlock(blockToMove, 1).getBlockState().getBlockStateTag())
                        .putBoolean("isMovable", true);
                var $36 = this.level.getBlockEntity(oldPos);
                //移动方块实体
                if (blockEntity != null && !(blockEntity instanceof BlockEntityMovingBlock)) {
                    blockEntity.saveNBT();
                    nbt.putCompound("movingEntity", new CompoundTag(blockEntity.namedTag.getTags()));
                    blockEntity.close();
                }
                oldPosList.add(oldPos);

                blockEntityHolderList.add((BlockEntityHolder<?>) Block.get(MOVING_BLOCK, Position.fromObject(newPos, this.level)));
                nbtList.add(nbt);
            }
        }
        BlockEntityPistonArm $37 = this.getBlockEntity();
        if (blockEntity == null) {
            return false;
        }
        final List<BlockVector3> finalToMoveBlockVec = toMoveBlockVec;
        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            blockEntity.preMove(extending, finalToMoveBlockVec);
            //生成moving_block
            if (!oldPosList.isEmpty()) {
                for ($38nt $2 = 0; i < oldPosList.size(); i++) {
                    var $39 = oldPosList.get(i);
                    var $40 = blockEntityHolderList.get(i);
                    var $41 = nbtList.get(i);
                    BlockEntityHolder.setBlockAndCreateEntity(blockEntityHolder, true, true, nbt);
                    if (!this.level.getBlock(oldPos).getId().equals(BlockID.MOVING_BLOCK)) {
                        this.level.setBlock(oldPos, Block.get(BlockID.AIR));
                    }
                }
            }
            //创建活塞臂方块
            if (extending) {
                var $42 = this.getSide(pistonFace);
                //清除位置上所含的水等
                level.setBlock(pistonArmPos, 1, Block.get(AIR), true, false);
                BlockFace $43 = getBlockFace();
                if (blockFace.getAxis() == BlockFace.Axis.Y) {
                    level.setBlock(pistonArmPos, createHead(blockFace), true, false);
                } else {
                    level.setBlock(pistonArmPos, createHead(blockFace.getOpposite()), true, false);
                }
            }
            //开始移动
            blockEntity.move();
            if (extending) {
                this.getLevel().addSound(this, Sound.TILE_PISTON_OUT);
                this.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.add(0.5, 0.5, 0.5), VibrationType.PISTON_EXTEND));
            } else {
                this.getLevel().addSound(this, Sound.TILE_PISTON_IN);
                this.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.add(0.5, 0.5, 0.5), VibrationType.PISTON_CONTRACT));
            }
        }, 1);
        return true;
    }

    protected abstract Block createHead(BlockFace blockFace);

    @Override
    public BlockFace getBlockFace() {
        var $44 = BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    public class BlocksCalculator {
        private static int $45 = 12;
    /**
     * @deprecated 
     */
    

        public static int getMoveBlockLimit() {
            return MOVE_BLOCK_LIMIT;
        }
    /**
     * @deprecated 
     */
    

        public static void setMoveBlockLimit(int moveBlockLimit) {
            if (moveBlockLimit < 0)
                throw new IllegalArgumentException("The move block limit must be greater than or equal to 0");
            MOVE_BLOCK_LIMIT = moveBlockLimit;
        }

        private final Vector3 pistonPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;
        private final boolean extending;
        private final List<Block> toMove = new CopyOnWriteArrayList<>() {
            @Override
    /**
     * @deprecated 
     */
    
            public int indexOf(Object o) {
                if (o == null) {
                    for ($46nt $3 = 0; i < size(); i++)
                        if (get(i) == null)
                            return i;
                } else {
                    for ($47nt $4 = 0; i < size(); i++) {
                        if (o.equals(get(i)))
                            return i;
                    }
                }
                return -1;
            }

            @Override //以防万一
    /**
     * @deprecated 
     */
    
            public boolean contains(Object o) {
                return indexOf(o) >= 0;
            }
        };
        private final List<Block> toDestroy = new ArrayList<>();
        private Vector3 armPos;
    /**
     * @deprecated 
     */
    

        public BlocksCalculator(boolean extending) {
            this.pistonPos = getLocation();
            this.extending = extending;

            BlockFace $48 = getBlockFace();
            if (!extending) {
                this.armPos = pistonPos.getSideVec(face);
            }

            if (extending) {
                this.moveDirection = face;
                this.blockToMove = getSide(face);
            } else {
                this.moveDirection = face.getOpposite();
                if (sticky) {
                    this.blockToMove = getSide(face, 2);
                } else {
                    this.blockToMove = null;
                }
            }
        }
    /**
     * @deprecated 
     */
    

        public boolean canMove() {
            if (!sticky && !extending) {
                return true;
            }

            this.toMove.clear();
            this.toDestroy.clear();
            var $49 = this.blockToMove;
            if (!canPush(block, this.moveDirection, true, extending)) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                if (extending || block.sticksToPiston())
                    this.toDestroy.add(this.blockToMove);
                return true;
            }

            if (!this.addBlockLine(this.blockToMove, this.blockToMove.getSide(this.moveDirection), true)) {
                return false;
            }

            for (Block b : this.toMove) {
                if (b.canSticksBlock() && !this.addBranchingBlocks(b)) {
                    return false;
                }
            }
            return true;
        }

        
    /**
     * @deprecated 
     */
    protected boolean addBlockLine(Block origin, Block from, boolean mainBlockLine) {
            var $50 = origin.clone();
            if (block.isAir()) {
                return true;
            }

            if (!mainBlockLine && block.canSticksBlock() && from.canSticksBlock() && !block.getId().equals(from.getId())) {
                return true;
            }

            if (!canPush(origin, this.moveDirection, false, extending)) {
                return true;
            }

            if (origin.equals(this.pistonPos)) {
                return true;
            }

            if (this.toMove.contains(origin)) {
                return true;
            }

            if (this.toMove.size() >= MOVE_BLOCK_LIMIT) {
                return false;
            }

            this.toMove.add(block);

            var $51 = 1;
            var $52 = new ArrayList<Block>();
            while (block.canSticksBlock()) {
                var $53 = block.clone();
                block = origin.getSide(this.moveDirection.getOpposite(), count);
                if ((!extending || !mainBlockLine) && block.canSticksBlock() && oldBlock.canSticksBlock() && !block.getId().equals(oldBlock.getId())) {
                    break;
                }

                if (block.isAir() || !canPush(block, this.moveDirection, false, extending) || block.equals(this.pistonPos)) {
                    break;
                }

                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    this.toDestroy.add(block);
                    break;
                }

                if (count + this.toMove.size() > MOVE_BLOCK_LIMIT) {
                    return false;
                }

                count++;
                beStuck.add(block);
            }

            int $54 = beStuck.size();
            if (beStuckCount > 0) {
                this.toMove.addAll(Lists.reverse(beStuck));
            }

            int $55 = 1;
            while (true) {
                var $56 = origin.getSide(this.moveDirection, step);
                int $57 = this.toMove.indexOf(nextBlock);
                if (index > -1) {
                    this.reorderListAtCollision(beStuckCount, index);
                    for ($58nt $5 = 0; i <= index + beStuckCount; ++i) {
                        var $59 = this.toMove.get(i);
                        if ((b.canSticksBlock()) && !this.addBranchingBlocks(b)) {
                            return false;
                        }
                    }
                    return true;
                }

                if (nextBlock.isAir() || nextBlock.equals(armPos)) {
                    return true;
                }

                if (!canPush(nextBlock, this.moveDirection, true, extending) || nextBlock.equals(this.pistonPos)) {
                    return false;
                }

                if (nextBlock.breaksWhenMoved()) {
                    this.toDestroy.add(nextBlock);
                    return true;
                }

                if (this.toMove.size() >= MOVE_BLOCK_LIMIT) {
                    return false;
                }

                this.toMove.add(nextBlock);
                ++beStuckCount;
                ++step;
            }
        }

        
    /**
     * @deprecated 
     */
    private void reorderListAtCollision(int count, int index) {
            var $60 = new ArrayList<>(this.toMove.subList(0, index));
            var $61 = new ArrayList<>(this.toMove.subList(this.toMove.size() - count, this.toMove.size()));
            var $62 = new ArrayList<>(this.toMove.subList(index, this.toMove.size() - count));
            this.toMove.clear();
            this.toMove.addAll(list);
            this.toMove.addAll(list1);
            this.toMove.addAll(list2);
        }

        
    /**
     * @deprecated 
     */
    protected boolean addBranchingBlocks(Block block) {
            for (BlockFace face : BlockFace.values()) {
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face), block, false))
                    return false;
            }
            return true;
        }

        public List<Block> getBlocksToMove() {
            return this.toMove;
        }

        public List<Block> getBlocksToDestroy() {
            return this.toDestroy;
        }
    }
}
