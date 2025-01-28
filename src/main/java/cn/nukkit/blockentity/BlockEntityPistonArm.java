package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPistonArmCollision;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityMoveByPistonEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a piston arm block entity.
 * Handles the movement and state of the piston arm.
 * 
 * @autor CreeperFace
 */
public class BlockEntityPistonArm extends BlockEntitySpawnable {

    public static final float MOVE_STEP = Utils.dynamic(0.25f);

    public BlockFace facing;
    public boolean extending;
    public boolean sticky;

    public byte state;

    public byte newState = 1;

    public List<BlockVector3> attachedBlocks;
    public boolean powered;
    public float progress;
    public float lastProgress = 1;

    public boolean finished = true;

    /**
     * Constructor for BlockEntityPistonArm.
     * 
     * @param chunk The chunk of the block entity.
     * @param nbt The compound tag.
     */
    public BlockEntityPistonArm(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    /**
     * Moves entities that collide with the piston arm.
     */
    protected void moveCollidedEntities() {
        if (this.closed || this.level == null) {
            return;
        }

        var pushDirection = this.extending ? facing : facing.getOpposite();
        for (var pos : this.attachedBlocks) {
            var blockEntity = this.level.getBlockEntity(pos.getSide(pushDirection));
            if (blockEntity instanceof BlockEntityMovingBlock be)
                be.moveCollidedEntities(this, pushDirection);
        }
        var bb = new SimpleAxisAlignedBB(0, 0, 0, 1, 1, 1).getOffsetBoundingBox(
                this.x + (pushDirection.getXOffset() * progress),
                this.y + (pushDirection.getYOffset() * progress),
                this.z + (pushDirection.getZOffset() * progress)
        ).addCoord(0, pushDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);
        for (var entity : this.level.getCollidingEntities(bb))
            moveEntity(entity, pushDirection);
    }

    /**
     * Moves an entity in the specified direction.
     * 
     * @param entity The entity to move.
     * @param moveDirection The direction to move the entity.
     */
    void moveEntity(Entity entity, BlockFace moveDirection) {
        if (moveDirection == BlockFace.DOWN)
            return;
        var diff = Math.abs(this.progress - this.lastProgress);
        if (diff == 0 || !entity.canBePushed() || entity instanceof Player)
            return;
        EntityMoveByPistonEvent event = new EntityMoveByPistonEvent(entity, entity.getPosition());
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        entity.onPushByPiston(this);
        if (entity.closed)
            return;
        entity.move(
                diff * moveDirection.getXOffset(),
                diff * moveDirection.getYOffset() * (moveDirection == BlockFace.UP ? 2 : 1),
                diff * moveDirection.getZOffset()
        );
    }

    /**
     * Performs the preparatory operations before a move.
     * This method initializes the state prior to movement, including setting whether the structure is extending or contracting,
     * progress, state, and updates relevant moving data.
     *
     * @param extending A boolean indicating whether is extending.
     * @param attachedBlocks A list of BlockVector3 representing the blocks attached to the moving block.
     */
    public void preMove(boolean extending, List<BlockVector3> attachedBlocks) {
        this.finished = false;
        this.extending = extending;
        this.lastProgress = this.progress = extending ? 0 : 1;
        this.state = this.newState = (byte) (extending ? 1 : 3);
        this.attachedBlocks = attachedBlocks;
        this.movable = false;
        updateMovingData(true);
    }

    /**
     * Moves the piston arm.
     * This method should be called after preMove.
     */
    public void move() {
        if (this.closed || this.level == null) {
            return;
        }

        this.lastProgress = this.extending ? 0 : 1;
        this.moveCollidedEntities();
        this.scheduleUpdate();
    }

    /**
     * The piston extension process lasts 2gt.
     * 
     * @return True if updated, false otherwise.
     */
    @Override
    public boolean onUpdate() {
        var hasUpdate = true;
        if (this.extending) {
            this.progress = Math.min(1, this.progress + MOVE_STEP);
            this.lastProgress = Math.min(1, this.lastProgress + MOVE_STEP);
        } else {
            this.progress = Math.max(0, this.progress - MOVE_STEP);
            this.lastProgress = Math.max(0, this.lastProgress - MOVE_STEP);
        }
        moveCollidedEntities();
        if (this.progress == this.lastProgress) {
            this.state = this.newState = (byte) (extending ? 2 : 0);
            var pushDirection = this.extending ? facing : facing.getOpposite();
            var redstoneUpdateList = new ArrayList<BlockVector3>();
            for (var pos : this.attachedBlocks) {
                redstoneUpdateList.add(pos);
                redstoneUpdateList.add(pos.getSide(pushDirection));
                var movingBlock = this.level.getBlockEntity(pos.getSide(pushDirection));
                if (movingBlock instanceof BlockEntityMovingBlock movingBlockBlockEntity) {
                    movingBlock.close();
                    var moved = movingBlockBlockEntity.getMovingBlock();
                    moved.position(movingBlock);
                    this.level.setBlock(movingBlock, 1, Block.get(BlockID.AIR), true, false);
                    this.level.setBlock(movingBlock, moved, true, true);
                    var movedBlockEntity = movingBlockBlockEntity.getMovingBlockEntityCompound();
                    if (movedBlockEntity != null) {
                        movedBlockEntity.putInt("x", movingBlock.getFloorX());
                        movedBlockEntity.putInt("y", movingBlock.getFloorY());
                        movedBlockEntity.putInt("z", movingBlock.getFloorZ());
                        BlockEntity.createBlockEntity(movedBlockEntity.getString("id"), this.level.getChunk(movingBlock.getChunkX(), movingBlock.getChunkZ()), movedBlockEntity);
                    }
                    moved.onUpdate(Level.BLOCK_UPDATE_MOVED);
                }
            }
            for (var update : redstoneUpdateList) {
                RedstoneComponent.updateAllAroundRedstone(new Position(update.x, update.y, update.z, this.level));
            }
            var pos = getSide(facing);
            if (!extending) {
                this.movable = true;
                if (this.level.getBlock(pos) instanceof BlockPistonArmCollision) {
                    this.level.setBlock(pos, 1, Block.get(Block.AIR), true, false);
                    this.level.setBlock(pos, Block.get(Block.AIR), true);
                }
            }
            this.level.updateAroundObserver(this);
            this.level.scheduleUpdate(this.getLevelBlock(), 1);
            this.attachedBlocks.clear();
            this.finished = true;
            hasUpdate = false;
            updateMovingData(false);
        }
        return super.onUpdate() || hasUpdate;
    }

    /**
     * Loads the NBT data for the block entity.
     */
    @Override
    public void loadNBT() {
        super.loadNBT();
        this.state = this.namedTag.getByte("State");
        this.newState = this.namedTag.getByte("NewState");
        if (namedTag.contains("Progress"))
            this.progress = namedTag.getFloat("Progress");
        if (namedTag.contains("LastProgress"))
            this.lastProgress = namedTag.getFloat("LastProgress");
        this.sticky = namedTag.getBoolean("Sticky");
        this.extending = namedTag.getBoolean("Extending");
        this.powered = namedTag.getBoolean("powered");
        if (namedTag.contains("facing")) {
            this.facing = BlockFace.fromIndex(namedTag.getInt("facing"));
        } else {
            var block = this.getLevelBlock();
            if (block instanceof Faceable faceable)
                this.facing = faceable.getBlockFace();
            else
                this.facing = BlockFace.NORTH;
        }
        attachedBlocks = new ObjectArrayList<>();
        if (namedTag.contains("AttachedBlocks")) {
            var blocks = namedTag.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                for (int i = 0; i < blocks.size(); i += 3) {
                    this.attachedBlocks.add(new BlockVector3(
                            blocks.get(i).data,
                            blocks.get(i + 1).data,
                            blocks.get(i + 2).data
                    ));
                }
            }
        } else namedTag.putList("AttachedBlocks", new ListTag<>());
    }

    /**
     * Saves the NBT data for the block entity.
     */
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("State", this.state);
        this.namedTag.putByte("NewState", this.newState);
        this.namedTag.putFloat("Progress", this.progress);
        this.namedTag.putFloat("LastProgress", this.lastProgress);
        this.namedTag.putBoolean("powered", this.powered);
        this.namedTag.putList("AttachedBlocks", getAttachedBlocks());
        this.namedTag.putInt("facing", this.facing.getIndex());
        this.namedTag.putBoolean("Sticky", this.sticky);
        this.namedTag.putBoolean("Extending", this.extending);
    }

    /**
     * Checks if the block entity is valid.
     * 
     * @return True if valid, false otherwise.
     */
    @Override
    public boolean isBlockEntityValid() {
        var blockId = getBlock().getId();
        return blockId.equals(BlockID.PISTON) || blockId.equals(BlockID.STICKY_PISTON);
    }

    /**
     * Returns the spawn compound tag for the block entity.
     * 
     * @return The spawn compound tag.
     */
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putBoolean("isMovable", this.movable)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putList("AttachedBlocks", getAttachedBlocks())
                .putList("BreakBlocks", new ListTag<>())
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);
    }

    /**
     * Returns the list of attached blocks.
     * 
     * @return The list of attached blocks.
     */
    protected ListTag<IntTag> getAttachedBlocks() {
        var attachedBlocks = new ListTag<IntTag>();
        for (var block : this.attachedBlocks) {
            attachedBlocks.add(new IntTag(block.x));
            attachedBlocks.add(new IntTag(block.y));
            attachedBlocks.add(new IntTag(block.z));
        }
        return attachedBlocks;
    }

    /**
     * Updates the moving data for the block entity.
     * 
     * @param immediately True if the update should be immediate, false otherwise.
     */
    public void updateMovingData(boolean immediately) {
        if (this.closed || this.level == null) {
            return;
        }

        var packet = this.getSpawnPacket();
        if (!immediately) {
            if (packet != null)
                this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        } else {
            Server.broadcastPacket(this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values(), packet);
        }
    }
}