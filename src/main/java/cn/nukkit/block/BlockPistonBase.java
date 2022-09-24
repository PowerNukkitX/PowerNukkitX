package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityMovingBlock;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRegistry;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static cn.nukkit.level.Level.BLOCK_UPDATE_NORMAL;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
@Log4j2
public abstract class BlockPistonBase extends BlockSolidMeta implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityPistonArm> {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    private static Set<Position> lockedBlocks = new HashSet<>();

    private static Map<Position,Position> lockedBy = new HashMap<>();
    private static Map<Position,Set<Position>> pistonUpdateListeners = new HashMap<>();

    public static void updatePistonsListenTo(Position pos){
        if (pistonUpdateListeners.containsKey(pos)){
            Set<Position> set = pistonUpdateListeners.get(pos);
            for (Position p : set){
                p.getLevel().scheduleUpdate(p.getLevelBlock(),1);
            }
            pistonUpdateListeners.remove(pos);
        }
    }

    public static void listenPistonUpdateTo(Position listen ,Position to){
        if (!pistonUpdateListeners.containsKey(to)) {
            pistonUpdateListeners.put(to, new HashSet<>());
        }
        Set<Position> set = pistonUpdateListeners.get(to);
        if (!set.contains(listen))
            set.add(listen);
    }

    public static boolean isBlockLocked(Position pos){
        return lockedBlocks.contains(pos);
    }

    public boolean sticky;

    public BlockPistonBase() {
        this(0);
    }

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

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.PISTON_ARM;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityPistonArm> getBlockEntityClass() {
        return BlockEntityPistonArm.class;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (player != null) {
            if (Math.abs(player.getFloorX() - this.x) <= 1 && Math.abs(player.getFloorZ() - this.z) <= 1) {
                double y = player.y + player.getEyeHeight();

                if (y - this.y > 2) {
                    this.setDamage(BlockFace.UP.getIndex());
                } else if (this.y - y > 0) {
                    this.setDamage(BlockFace.DOWN.getIndex());
                } else {
                    this.setDamage(player.getHorizontalFacing().getIndex());
                }
            } else {
                this.setDamage(player.getHorizontalFacing().getIndex());
            }
        }

        if(this.level.getBlockEntity(this) != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);
            log.warn("Found unused BlockEntity at world={} x={} y={} z={} whilst attempting to place piston, closing it.", blockEntity.getLevel().getName(), blockEntity.getX(), blockEntity.getY(), blockEntity.getZ());
            blockEntity.saveNBT();
            blockEntity.close();
        }

        CompoundTag nbt = new CompoundTag()
                .putInt("facing", this.getBlockFace().getIndex())
                .putBoolean("Sticky", this.sticky)
                .putBoolean("powered", isGettingPower());


        BlockEntityPistonArm piston = BlockEntityHolder.setBlockAndCreateEntity(this, true, true, nbt);
        if (piston == null) {
            return false;
        }

        this.checkState(isGettingPower());
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        this.level.setBlock(this, new BlockAir(), true, true);
        updatePistonsListenTo(new Position(this.getX(), this.getY(), this.getZ(), this.getLevel()));
        //        locked-block locked-by
        for(Map.Entry<Position,Position> entry : lockedBy.entrySet().toArray(new Map.Entry[0])){
            if (entry.getValue().equals(this)){
                lockedBlocks.remove(entry.getKey());
                lockedBy.remove(entry.getKey());
            }
        }

        Block block = this.getSide(getBlockFace());

        if (block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == this.getBlockFace()) {
            block.onBreak(item);
        }
        return true;
    }

    public boolean isExtended() {
        BlockFace face = getBlockFace();
        Block block = getSide(face);

        return block instanceof BlockPistonHead && ((BlockPistonHead) block).getBlockFace() == face;
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered + update all around redstone torches, " +
            "even if the piston can't move.", since = "1.4.0.0-PN")
    public int onUpdate(int type) {
        if (type != BLOCK_UPDATE_NORMAL && type != Level.BLOCK_UPDATE_REDSTONE && type != Level.BLOCK_UPDATE_SCHEDULED) {
            return 0;
        } else {
            if (!this.level.getServer().isRedstoneEnabled()) {
                return 0;
            }

            if (lockedBlocks.contains(new Position(this.getX(), this.getY(), this.getZ(), this.getLevel()))) {
                return 0;
            }

            // We can't use getOrCreateBlockEntity(), because the update method is called on block place,
            // before the "real" BlockEntity is set. That means, if we'd use the other method here,
            // it would create two BlockEntities.
            BlockEntityPistonArm arm = this.getBlockEntity();

            boolean powered = this.isGettingPower();
            this.updateAroundRedstoneTorches(powered);

            if (arm == null || !arm.finished)
                return 0;

            if (arm.state % 2 == 0 && arm.powered != powered && checkState(powered)) {

                arm.powered = powered;

                if (arm.chunk != null) {
                    arm.chunk.setChanged();
                }
            }

            return type;
        }
    }

    private void updateAroundRedstoneTorches(boolean powered) {
        for (BlockFace side : BlockFace.values()) {
            if ((getSide(side) instanceof BlockRedstoneTorch && powered)
                    || (getSide(side) instanceof BlockRedstoneTorchUnlit && !powered)) {
                BlockTorch torch = (BlockTorch) getSide(side);

                BlockTorch.TorchAttachment torchAttachment = torch.getTorchAttachment();
                Block support = torch.getSide(torchAttachment.getAttachedFace());

                if (support.getLocation().equals(this.getLocation())) {
                    torch.onUpdate(Level.BLOCK_UPDATE_REDSTONE);
                }
            }
        }
    }

    private boolean checkState(Boolean isPowered) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return false;
        }

        if (isPowered == null) {
            isPowered = this.isGettingPower();
        }

        if (isPowered && !isExtended()) {
            if (!this.doMove(true)) {
                return false;
            }

            this.getLevel().addSound(this, Sound.TILE_PISTON_OUT);

            this.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.add(0.5, 0.5, 0.5), VibrationType.PISTON_EXTEND));

            return true;
        } else if (!isPowered && isExtended()) {
            if (!this.doMove(false)) {
                return false;
            }

            this.getLevel().addSound(this, Sound.TILE_PISTON_IN);

            this.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.add(0.5, 0.5, 0.5), VibrationType.PISTON_CONTRACT));

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

            if (b.getId() == Block.REDSTONE_WIRE && b.getDamage() > 0 && b.y >= this.getY()) {
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

        BlocksCalculator calculator = new BlocksCalculator(level, this, getBlockFace(), extending, sticky);

        boolean canMove = calculator.canMove();

        if (!canMove) {
            Position pos = new Position(this.getX(), this.getY(), this.getZ(), this.getLevel());
            if(calculator.blockedByPistonHeadOrLockedBlock) {
                listenPistonUpdateTo(pos,calculator.blockedPos);
            }
            return false;
        }

        calculator.recordLockedBlocks();
        calculator.lockBlocks();

        List<BlockVector3> attached = Collections.emptyList();

        BlockPistonEvent event = new BlockPistonEvent(this, direction, calculator.getBlocksToMove(), calculator.getBlocksToDestroy(), extending);
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            calculator.unlockBlocks();
            return false;
        }

        if (canMove && (this.sticky || extending)) {
            List<Block> destroyBlocks = calculator.getBlocksToDestroy();
            for (int i = destroyBlocks.size() - 1; i >= 0; --i) {
                Block block = destroyBlocks.get(i);
                this.level.useBreakOn(block, null, null, false);
            }

            List<Block> newBlocks = calculator.getBlocksToMove();

            attached = newBlocks.stream().map(Vector3::asBlockVector3).collect(Collectors.toList());

            BlockFace side = extending ? direction : direction.getOpposite();

            List<CompoundTag> tags = new ArrayList<>();
            for (Block oldBlock : newBlocks) {
                CompoundTag tag = new CompoundTag();
                BlockEntity be = this.level.getBlockEntity(oldBlock);
                if (be != null && !(be instanceof BlockEntityMovingBlock)) {
                    be.saveNBT();
                    tag = new CompoundTag(be.namedTag.getTags());

                    be.close();
                }
                tags.add(tag);
            }

            int i = 0;
            for (Block newBlock : newBlocks) {
                Block oldPos = newBlock.getBlock();
                newBlock.position(newBlock.add(0).getSide(side));

                CompoundTag nbt = new CompoundTag()
                        .putInt("pistonPosX", this.getFloorX())
                        .putInt("pistonPosY", this.getFloorY())
                        .putInt("pistonPosZ", this.getFloorZ())
                        .putCompound("movingBlock", new CompoundTag()
                                .putInt("id", newBlock.getId()) //only for nukkit purpose
                                .putInt("meta", newBlock.getDamage()) //only for nukkit purpose
                                .putShort("val", newBlock.getDamage())
                                .putString("name", BlockStateRegistry.getPersistenceName(newBlock.getId()))
                        );

                if (!tags.get(i).isEmpty()) {
                    nbt.putCompound("movingEntity", tags.get(i));
                }

                BlockEntityHolder.setBlockAndCreateEntity((BlockEntityHolder<?>) BlockState.of(BlockID.MOVING_BLOCK).getBlock(newBlock),
                        true, true, nbt);

                if (this.level.getBlockIdAt(oldPos.getFloorX(), oldPos.getFloorY(), oldPos.getFloorZ()) != BlockID.MOVING_BLOCK) {
                    this.level.setBlock(oldPos, Block.get(BlockID.AIR));
                }
                i++;
            }
        }

        if (extending) {
            this.level.setBlock(this.getSide(direction), createHead(this.getDamage()));
        }

        BlockEntityPistonArm blockEntity = getOrCreateBlockEntity();
        blockEntity.move(extending, attached,calculator);
        return true;
    }

    @PowerNukkitOnly
    protected BlockPistonHead createHead(int damage) {
        return (BlockPistonHead) Block.get(getPistonHeadBlockId(), damage);
    }

    @PowerNukkitOnly
    public abstract int getPistonHeadBlockId();

    @PowerNukkitOnly
    public static boolean canPush(Block block, BlockFace face, boolean destroyBlocks, boolean extending) {
        if (
                block.getY() >= block.level.getMinHeight() && (face != BlockFace.DOWN || block.getY() != block.level.getMinHeight()) &&
                        block.getY() <= block.level.getMaxHeight() - 1 && (face != BlockFace.UP || block.getY() != block.level.getMaxHeight() - 1)
        ) {
            if (lockedBlocks.contains(new Position(block.getX(), block.getY(), block.getZ(), block.level))) {
                return false;
            }

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

        private final Position pistonPos;
        private Vector3 armPos;
        private final Block blockToMove;
        private final BlockFace moveDirection;
        private final boolean extending;
        private final boolean sticky;

        private final List<Block> toMove = new ArrayList<>();
        private final List<Block> toDestroy = new ArrayList<>();
        private final Set<Position> toLock = new HashSet<>();
        private boolean blockedByPistonHeadOrLockedBlock = false;
        private Position blockedPos = null;

        /**
         * @param level Unused, needed for compatibility with Cloudburst Nukkit plugins
         */
        public BlocksCalculator(Level level, Block block, BlockFace facing, boolean extending) {
            this(level, block, facing, extending, false);
        }

        /**
         * @param level Unused, needed for compatibility with Cloudburst Nukkit plugins
         */
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public BlocksCalculator(Level level, Block pos, BlockFace face, boolean extending, boolean sticky) {
            this.pistonPos = pos.getLocation();
            this.extending = extending;
            this.sticky = sticky;

            if (!extending) {
                this.armPos = pistonPos.getSide(face);
            }

            if (extending) {
                this.moveDirection = face;
                this.blockToMove = pos.getSide(face);
            } else {
                this.moveDirection = face.getOpposite();
                if (sticky) {
                    this.blockToMove = pos.getSide(face, 2);
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

            if (!this.addBlockLine(this.blockToMove, this.blockToMove.getSide(this.moveDirection.getOpposite()), true)) {
                return false;
            }

            for (int i = 0; i < this.toMove.size(); ++i) {
                Block b = this.toMove.get(i);

                int blockId = b.getId();
                if ((blockId == SLIME_BLOCK || blockId == HONEY_BLOCK) && !this.addBranchingBlocks(b)) {
                    return false;
                }
            }

            return true;
        }

        @PowerNukkitXOnly
       @Since("1.6.0.0-PNX")
        public boolean canPush(Block block, BlockFace face, boolean destroyBlocks, boolean extending) {
            boolean canPush = BlockPistonBase.canPush(block, face, destroyBlocks, extending);
            if (!canPush) {
                Position blockedPos = new Position(block.getX(), block.getY(), block.getZ(), block.level);
                if(block instanceof BlockPistonHead) {
                    this.blockedByPistonHeadOrLockedBlock = true;
                    BlockPistonHead head = (BlockPistonHead) block;
                    Block base = head.getSide(head.getFacing().getOpposite());
                    this.blockedPos = new Position(base.getX(), base.getY(), base.getZ(), base.level);
                    return false;
                }
                if (block instanceof BlockPistonBase) {
                    this.blockedByPistonHeadOrLockedBlock = true;
                    this.blockedPos = blockedPos;
                    return false;
                }
                if(lockedBlocks.contains(blockedPos)) {
                    this.blockedByPistonHeadOrLockedBlock = true;
                    this.blockedPos = blockedPos;
                    return false;
                }
            }
            return canPush;
        }

        @PowerNukkitXOnly
       @Since("1.6.0.0-PNX")
        public Set<Position> getLockedBlocks(){
            return this.toLock;
        }

        @PowerNukkitXOnly
       @Since("1.6.0.0-PNX")
        public void recordLockedBlocks(){
            this.toLock.clear();
            this.toMove.forEach(block -> toLock.add(new Position(block.getX(), block.getY(), block.getZ(), block.level)));
            this.toMove.forEach(block -> {
                Block blockForward = block.getSide(this.moveDirection);
                Position pos = new Position(blockForward.getX(), blockForward.getY(), blockForward.getZ(), blockForward.level);
                toLock.add(pos);
            });
            this.toLock.add(new Position(this.pistonPos.getX(), this.pistonPos.getY(), this.pistonPos.getZ(),this.pistonPos.level));
            Position pistonForward = this.pistonPos.getSide(this.moveDirection);
            this.toLock.add(new Position(pistonForward.getX(), pistonForward.getY(), pistonForward.getZ(),pistonForward.level));
        }

        private boolean addBlockLine(Block origin, Block from, boolean mainBlockLine) {
            Block block = origin.clone();

            if (block.getId() == AIR) {
                return true;
            }

            if (!mainBlockLine && (block.getId() == SLIME_BLOCK && from.getId() == HONEY_BLOCK
                    || block.getId() == HONEY_BLOCK && from.getId() == SLIME_BLOCK)) {
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

            while (block.getId() == SLIME_BLOCK || block.getId() == HONEY_BLOCK) {
                Block oldBlock = block.clone();
                block = origin.getSide(this.moveDirection.getOpposite(), count);

                if (!extending && (block.getId() == SLIME_BLOCK && oldBlock.getId() == HONEY_BLOCK
                        || block.getId() == HONEY_BLOCK && oldBlock.getId() == SLIME_BLOCK)) {
                    break;
                }

                if (block.getId() == AIR || !canPush(block, this.moveDirection, false, extending) || block.equals(this.pistonPos)) {
                    break;
                }

                if (block.breaksWhenMoved() && block.sticksToPiston()) {
                    this.toDestroy.add(block);
                    break;
                }

                if (count + this.toMove.size() > 12) {
                    return false;
                }
                count++;

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

                        if ((b.getId() == SLIME_BLOCK || b.getId() == HONEY_BLOCK) && !this.addBranchingBlocks(b)) {
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
                if (face.getAxis() != this.moveDirection.getAxis() && !this.addBlockLine(block.getSide(face), block, false)) {
                    return false;
                }
            }

            return true;
        }

        public List<Block> getBlocksToMove() {
            return this.toMove.stream().map(Block::clone).collect(Collectors.toList());
        }

        public List<Block> getBlocksToDestroy() {
            return this.toDestroy.stream().map(Block::clone).collect(Collectors.toList());
        }

        @PowerNukkitXOnly
       @Since("1.6.0.0-PNX")
        public void lockBlocks(){
            for (Position pos : toLock){
                lockedBlocks.add(pos);
                lockedBy.put(pos,this.pistonPos);
            }
        }

        @PowerNukkitXOnly
       @Since("1.6.0.0-PNX")
        public void unlockBlocks() {
            for (Position pos : toLock) {
                lockedBlocks.remove(pos);
                lockedBy.remove(pos);
            }
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        BlockFace face = BlockFace.fromIndex(this.getDamage());

        return face.getHorizontalIndex() >= 0 ? face.getOpposite() : face;
    }
}