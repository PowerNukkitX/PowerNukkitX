package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
public abstract class BlockPistonBase extends BlockTransparentMeta implements Faceable, RedstoneComponent, BlockEntityHolder<BlockEntityPistonArm> {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    public boolean sticky = false;

    public BlockPistonBase(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityPistonArm> getBlockEntityClass() {
        return BlockEntityPistonArm.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.PISTON_ARM;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, BlockFace.UP);
                } else if (this.y - y > 0) {
                    this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, BlockFace.DOWN);
                } else {
                    this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, player.getHorizontalFacing());
                }
            } else {
                this.setPropertyValue(CommonBlockProperties.FACING_DIRECTION, player.getHorizontalFacing());
            }
        }
        this.level.setBlock(block, this, true, true);

        CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.PISTON_ARM)
                .putInt("facing", this.getBlockFace().getIndex())
                .putBoolean("Sticky", this.sticky);

        BlockEntityPistonArm piston = (BlockEntityPistonArm) BlockEntity.createBlockEntity(BlockEntity.PISTON_ARM, this.level.getChunk(getChunkX(), getChunkZ()), nbt);
        piston.powered = isGettingPower();

        this.checkState(piston.powered);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, true);

        Block block = this.getSide(getBlockFace());

        if (block instanceof BlockPistonHead b && b.getBlockFace() == this.getBlockFace()) {
            block.onBreak(item);
        }
        return true;
    }

    public boolean isExtended() {
        BlockFace face = getBlockFace();
        Block block = getSide(face);

        return block instanceof BlockPistonHead b && b.getBlockFace() == face;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_REDSTONE) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            level.scheduleUpdate(this, 1);
            return type;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            BlockEntity blockEntity = this.level.getBlockEntity(this);
            if (blockEntity instanceof BlockEntityPistonArm arm) {
                boolean powered = this.isGettingPower();

                if (arm.state % 2 == 0 && arm.powered != powered && checkState(powered)) {
                    arm.powered = powered;

                    if (arm.chunk != null) {
                        arm.chunk.setChanged();
                    }

                    //推出未成功
                    if (powered && !isExtended()) {
                        //下一个计划刻自检
                        level.scheduleUpdate(this, 1);
                    }

                    return type;
                }

                //上一次推出未成功
                if (type == Level.BLOCK_UPDATE_SCHEDULED && powered && !isExtended() && !checkState(true)) {
                    //依然不成功，下一个计划刻继续自检
                    level.scheduleUpdate(this, 1);
                }
            }

            return type;
        }

        return 0;
    }

    private boolean checkState(Boolean isPowered) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return false;
        }

        if (isPowered == null) {
            isPowered = this.isGettingPower();
        }

        BlockFace face = getBlockFace();
        Block block = getSide(face);

        boolean isExtended;
        if (block instanceof BlockPistonHead b) {
            if (b.getBlockFace() != face) {
                return false;
            }

            isExtended = true;
        } else {
            isExtended = false;
        }

        if (isPowered && !isExtended) {
            if (!this.doMove(true)) {
                return false;
            }

            this.getLevel().addSound(this, Sound.TILE_PISTON_OUT);
            return true;
        } else if (!isPowered && isExtended) {
            if (!this.doMove(false)) {
                return false;
            }

            this.getLevel().addSound(this, Sound.TILE_PISTON_IN);
            return true;
        }

        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Override
    public boolean isGettingPower() {
        BlockFace face = getBlockFace();

        for (BlockFace side : BlockFace.values()) {
            if (side == face) {
                continue;
            }

            Block b = this.getSide(side);

            if (b.getId() == Block.REDSTONE_WIRE && b.getDamage() > 0) {
                return true;
            }

            if (this.level.isSidePowered(b, side)) {
                return true;
            }
        }

        return false;
    }

    private boolean doMove(boolean extending) {
        BlockFace direction = getBlockFace();

        BlocksCalculator calculator = new BlocksCalculator(extending);

        boolean canMove = calculator.canMove();

        if (!canMove && extending) {
            return false;
        }

        List<BlockVector3> attached = Collections.emptyList();

        BlockPistonEvent event = new BlockPistonEvent(this, direction, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        if (canMove && (this.sticky || extending)) {
            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                level.setBlock(block, 1, Block.get(BlockID.AIR), true, false);
                this.level.useBreakOn(block);
            }

            List<Block> newBlocks = calculator.getBlocksToMove();

            attached = newBlocks.stream().map(Vector3::asBlockVector3).collect(Collectors.toList());

            BlockFace side = extending ? direction : direction.getOpposite();

            var oldPosList = new ArrayList<Vector3>();
            var blockEntityHolderList = new ArrayList<BlockEntityHolder<?>>();
            var nbtList = new ArrayList<CompoundTag>();

            for (Block newBlock : newBlocks) {
                Vector3 oldPos = new Vector3(newBlock.x, newBlock.y, newBlock.z);
                Vector3 newPos = newBlock.getSidePos(side);

                BlockEntity blockEntity = this.level.getBlockEntity(oldPos);

                level.setBlock(newPos, 1, Block.get(AIR), true, false);

                CompoundTag movingBlockTag = new CompoundTag()
                        .putInt("id", newBlock.getId()) //only for nukkit purpose
                        .putInt("meta", newBlock.getDamage()); //only for nukkit purpose
                String blockName = BlockStateRegistry.getPersistenceName(newBlock.getId());
                movingBlockTag.putString("name", blockName)
                            .putShort("val", newBlock.getDamage());

                CompoundTag nbt = BlockEntity.getDefaultCompound(newPos, BlockEntity.MOVING_BLOCK)
                        .putInt("pistonPosX", this.getFloorX())
                        .putInt("pistonPosY", this.getFloorY())
                        .putInt("pistonPosZ", this.getFloorZ())
                        .putCompound("movingBlock", movingBlockTag);

                if (blockEntity != null && !(blockEntity instanceof BlockEntityMovingBlock)) {
                    blockEntity.saveNBT();

                    nbt.putCompound("movingEntity", new CompoundTag(blockEntity.namedTag.getTags()));

                    if (blockEntity instanceof InventoryHolder) {
                        ((InventoryHolder) blockEntity).getInventory().clearAll();
                    }

                    blockEntity.close();
                }

                oldPosList.add(oldPos);
                blockEntityHolderList.add((BlockEntityHolder<?>) BlockState.of(BlockID.MOVING_BLOCK).getBlock(Position.fromObject(newPos, this.level)));
                nbtList.add(nbt);
            }

            for (int i = 0; i < oldPosList.size(); i++) {
                Vector3 oldPos = oldPosList.get(i);
                BlockEntityHolder<?> blockEntityHolder = blockEntityHolderList.get(i);
                CompoundTag nbt = nbtList.get(i);

                BlockEntityHolder.setBlockAndCreateEntity(blockEntityHolder, true, true, nbt);

                if (this.level.getBlock(oldPos).getId() != BlockID.MOVING_BLOCK) {
                    this.level.setBlock(oldPos, Block.get(BlockID.AIR));
                }
            }
        }

        if (extending) {
            Vector3 pos = this.getSide(direction);
            level.setBlock(pos, 1, Block.get(AIR), true, false);
            if (this.sticky && Block.list[STICKY_PISTON_ARM_COLLISION] != null) {
                this.level.setBlock(pos, get(STICKY_PISTON_ARM_COLLISION, this.getDamage()), true);
            } else {
                this.level.setBlock(pos, get(PISTON_ARM_COLLISION, this.getDamage()), true);
            }
        }

        BlockEntityPistonArm blockEntity = (BlockEntityPistonArm) this.level.getBlockEntity(this);
        blockEntity.move(extending, attached);
        return true;
    }

    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks, boolean extending) {
        var min = block.level.getMinHeight();
        var max = block.level.getMaxHeight() - 1;
        if (
                block.getY() >= min && (face != BlockFace.DOWN || block.getY() != min) &&
                        block.getY() <= max && (face != BlockFace.UP || block.getY() != max)
        ) {
            if (extending && !block.canBePushed() || !extending && !block.canBePulled()) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                return destroyBlocks || block.sticksToPiston();
            }

            BlockEntity be = block.level.getBlockEntity(block);
            return be == null || be.isMovable();
        }

        return false;
    }

    public class BlocksCalculator {

        private final Vector3 pistonPos;
        private Vector3 armPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;
        private final boolean extending;

        private final List<Block> toMove = new ArrayList<>() {
            @Override
            public int indexOf(Object o) {
                if (o == null) {
                    for (int i = 0; i < size(); i++)
                        if (get(i) == null)
                            return i;
                } else {
                    for (int i = 0; i < size(); i++) {
                        if (o.equals(get(i)))
                            return i;
                    }
                }
                return -1;
            }

            @Override //以防万一
            public boolean contains(Object o) {
                return indexOf(o) >= 0;
            }
        };
        private final List<Block> toDestroy = new ArrayList<>();

        public BlocksCalculator(boolean extending) {
            this.pistonPos = getLocation();
            this.extending = extending;

            BlockFace face = getBlockFace();
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

        public boolean canMove() {
            if (!sticky && !extending) {
                return true;
            }

            this.toMove.clear();
            this.toDestroy.clear();
            Block block = this.blockToMove;

            if (!canPush(block, this.moveDirection, true, extending)) {
                return false;
            }

            if (block.breaksWhenMoved()) {
                if (extending || block.sticksToPiston()) {
                    this.toDestroy.add(this.blockToMove);
                }

                return true;
            }

            if (!this.addBlockLine(this.blockToMove, this.moveDirection)) {
                return false;
            }

            for (int i = 0; i < this.toMove.size(); ++i) {
                Block b = this.toMove.get(i);

                if (b.getId() == SLIME_BLOCK && !this.addBranchingBlocks(b)) {
                    return false;
                }
            }

            return true;
        }

        private boolean addBlockLine(Block origin, BlockFace from) {
            Block block = origin.clone();

            if (block.getId() == AIR) {
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

            if (this.toMove.size() >= 12) {
                return false;
            }

            this.toMove.add(block);

            int count = 1;
            List<Block> sticked = new ArrayList<>();

            while (block.getId() == SLIME_BLOCK) {
                block = origin.getSide(this.moveDirection.getOpposite(), count);

                if (block.getId() == AIR || !canPush(block, this.moveDirection, false, extending) || block.equals(this.pistonPos)) {
                    break;
                }

                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    this.toDestroy.add(block);
                    break;
                }

                if (++count + this.toMove.size() > 12) {
                    return false;
                }

                sticked.add(block);
            }

            int stickedCount = sticked.size();

            if (stickedCount > 0) {
                this.toMove.addAll(Lists.reverse(sticked));
            }

            int step = 1;

            while (true) {
                Block nextBlock = origin.getSide(this.moveDirection, step);
                int index = this.toMove.indexOf(nextBlock);

                if (index > -1) {
                    this.reorderListAtCollision(stickedCount, index);

                    for (int i = 0; i <= index + stickedCount; ++i) {
                        Block b = this.toMove.get(i);

                        if (b.getId() == SLIME_BLOCK && !this.addBranchingBlocks(b)) {
                            return false;
                        }
                    }

                    return true;
                }

                if (nextBlock.getId() == AIR || nextBlock.equals(armPos)) {
                    return true;
                }

                if (!canPush(nextBlock, this.moveDirection, true, extending) || nextBlock.equals(this.pistonPos)) {
                    return false;
                }

                if (nextBlock.breaksWhenMoved()) {
                    this.toDestroy.add(nextBlock);
                    return true;
                }

                if (this.toMove.size() >= 12) {
                    return false;
                }

                this.toMove.add(nextBlock);
                ++stickedCount;
                ++step;
            }
        }

        private void reorderListAtCollision(int count, int index) {
            List<Block> list = new ArrayList<>(this.toMove.subList(0, index));
            List<Block> list1 = new ArrayList<>(this.toMove.subList(this.toMove.size() - count, this.toMove.size()));
            List<Block> list2 = new ArrayList<>(this.toMove.subList(index, this.toMove.size() - count));
            this.toMove.clear();
            this.toMove.addAll(list);
            this.toMove.addAll(list1);
            this.toMove.addAll(list2);
        }

        private boolean addBranchingBlocks(Block block) {
            for (BlockFace face : BlockFace.values()) {
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face), face)) {
                    return false;
                }
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

    @Override
    public BlockFace getBlockFace() {
        BlockFace face = BlockFace.fromIndex(this.getDamage());

        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
