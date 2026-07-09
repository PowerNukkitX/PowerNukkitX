package org.powernukkitx.blockentity;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockEntityHolder;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockPistonArmCollision;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.entity.EntityMoveByPistonEvent;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.Position;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.utils.Faceable;
import org.powernukkitx.utils.RedstoneComponent;
import org.powernukkitx.utils.Utils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author CreeperFace
 */
public class BlockEntityPistonArm extends BlockEntitySpawnable {

    public static final float MOVE_STEP = Utils.dynamic(0.5f);

    public BlockFace facing;
    public boolean extending;
    public boolean sticky;

    public byte state;

    public byte newState = 1;

    public List<BlockVector3> attachedBlocks;
    public boolean powered;
    public boolean hasPendingPower;
    public boolean pendingPowered;
    public float progress;
    public float lastProgress = 1;

    private final Set<Long> movedEntitiesThisTick = new HashSet<>();
    private final Set<Long> affectedEntitiesThisTick = new HashSet<>();

    public boolean finished = true;

    public BlockEntityPistonArm(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void moveCollidedEntities() {
        if (this.closed || this.level == null) {
            return;
        }

        var pushDirection = this.extending ? facing : facing.getOpposite();
        this.movedEntitiesThisTick.clear();
        this.affectedEntitiesThisTick.clear();
        for (var pos : this.attachedBlocks) {
            var blockEntity = this.level.getBlockEntity(pos.getSide(pushDirection));
            if (blockEntity instanceof BlockEntityMovingBlock be)
                be.moveCollidedEntities(this, pushDirection);
        }
        var bb = new SimpleAxisAlignedBB(0, 0, 0, 1, 1, 1).getOffsetBoundingBox(
                this.x + (pushDirection.getXOffset() * progress),
                this.y + (pushDirection.getYOffset() * progress),
                this.z + (pushDirection.getZOffset() * progress)
                // Moves entities standing on moving blocks
        ).addCoord(0, pushDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);
        for (var entity : this.level.getCollidingEntities(bb))
            moveEntity(entity, pushDirection);
    }

    boolean moveEntity(Entity entity, BlockFace moveDirection) {
        // No downward force is required
        if (moveDirection == BlockFace.DOWN)
            return false;
        var diff = Math.abs(this.progress - this.lastProgress);
        // Player clients automatically handle movement
        if (diff == 0 || !entity.canBePushedByPiston() || entity instanceof Player)
            return false;
        if (!this.markEntityAffected(entity) || this.movedEntitiesThisTick.contains(entity.getId()))
            return false;
        EntityMoveByPistonEvent event = new EntityMoveByPistonEvent(entity, entity.getPosition());
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;
        entity.onPushByPiston(this);
        if (entity.closed)
            return false;
        this.movedEntitiesThisTick.add(entity.getId());
        // Need to counteract gravity
        entity.move(
                diff * moveDirection.getXOffset(),
                diff * moveDirection.getYOffset() * (moveDirection == BlockFace.UP ? 2 : 1),
                diff * moveDirection.getZOffset()
        );
        return true;
    }

    boolean markEntityAffected(Entity entity) {
        var diff = Math.abs(this.progress - this.lastProgress);
        if (diff == 0 || !entity.canBePushedByPiston()) {
            return false;
        }
        return this.affectedEntitiesThisTick.add(entity.getId());
    }

    /**
     * Performs the preparatory operations before a move.
     * This method initializes the state prior to movement, including setting whether the structure is extending or contracting,
     * progress, state, and updates relevant moving data.
     *
     * @param extending      A boolean indicating whether is extending
     * @param attachedBlocks A list of BlockVector3 representing the blocks attached to the moving block.
     */
    public void preMove(boolean extending, List<BlockVector3> attachedBlocks) {
        this.finished = false; // Initialize movement as unfinished
        this.extending = extending; // Set the extending status
        this.lastProgress = this.progress = extending ? 0 : 1; // Set progress: 0 for extending, 1 for contracting
        this.state = this.newState = (byte) (extending ? 1 : 3); // Set current and new states: 1 for extending, 3 for contracting
        this.attachedBlocks = attachedBlocks; // Set the attached blocks list
        this.movable = false; // Set the structure as immovable
        // Update moving data immediately to ensure timeliness
        updateMovingData(true);
    }


    // You need to call preMove first
    public void move() {
        if (this.closed || this.level == null) {
            return;
        }

        // Start pushing
        this.lastProgress = this.extending ? 0 : 1;
        this.moveCollidedEntities();
        this.scheduleUpdate();
    }

    /** The piston extension process lasts two ticks. */
    @Override
    public boolean onUpdate() {

        // This bool marks whether the next gt needs to continue updating
        var hasUpdate = true;
        // Promotion process
        this.lastProgress = this.progress;
        if (this.extending) {
            this.progress = Math.min(1, this.progress + MOVE_STEP);
        } else {
            this.progress = Math.max(0, this.progress - MOVE_STEP);
        }
        moveCollidedEntities();
        if ((this.extending && this.progress >= 1) || (!this.extending && this.progress <= 0)) {
            finishMove();
            hasUpdate = false;
        }
        return super.onUpdate() || hasUpdate;
    }

    public void finishMove() {
        if (this.closed || this.level == null || this.finished) {
            return;
        }

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
                moved.setLevel(this.level);
                this.level.setBlock(movingBlock, 1, Block.get(BlockID.AIR), true, false);
                // Common Block Updates
                var movedBlockEntity = movingBlockBlockEntity.getMovingBlockEntityCompound();
                if (moved instanceof BlockEntityHolder<?> holder && movedBlockEntity != null) {
                    movedBlockEntity.putInt("x", movingBlock.getFloorX());
                    movedBlockEntity.putInt("y", movingBlock.getFloorY());
                    movedBlockEntity.putInt("z", movingBlock.getFloorZ());
                    BlockEntityHolder.setBlockAndCreateEntity(holder, false, true, movedBlockEntity);
                } else this.level.setBlock(movingBlock, moved, true, true);
                // Piston Update
                moved.onUpdate(Level.BLOCK_UPDATE_MOVED);
            }
        }
        for (var update : redstoneUpdateList) {
            // Redstone Update
            RedstoneComponent.updateAllAroundRedstone(new Position(update.x, update.y, update.z, this.level));
        }
        var pos = getSide(facing);
        if (!extending) {
            // The unextended piston can be pushed
            this.movable = true;
            if (this.level.getBlock(pos) instanceof BlockPistonArmCollision) {
                this.level.setBlock(pos, 1, Block.get(Block.AIR), true, false);
                // Block Updates
                this.level.setBlock(pos, Block.get(Block.AIR), true);
            }
        }
        // Updates observers that are in direct contact with the piston
        this.level.updateAroundObserver(this);
        // Check again at the next moment to prevent mistakes
        this.level.scheduleUpdate(this.getLevelBlock(), 1);
        this.attachedBlocks.clear();
        this.finished = true;
        updateMovingData(false);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.state = this.nbt.getByte("State");
        this.newState = this.nbt.getByte("NewState");
        if (nbt.contains("Progress"))
            this.progress = nbt.getFloat("Progress");
        if (nbt.contains("LastProgress"))
            this.lastProgress = nbt.getFloat("LastProgress");
        this.sticky = nbt.getBoolean("Sticky");
        this.extending = nbt.getBoolean("Extending");
        this.powered = nbt.getBoolean("powered");
        this.hasPendingPower = nbt.getBoolean("hasPendingPower");
        this.pendingPowered = nbt.getBoolean("pendingPowered");
        if (nbt.contains("facing")) {
            this.facing = BlockFace.fromIndex(nbt.getInt("facing"));
        } else {
            var block = this.getLevelBlock();
            if (block instanceof Faceable faceable)
                this.facing = faceable.getBlockFace();
            else
                this.facing = BlockFace.NORTH;
        }
        attachedBlocks = new ObjectArrayList<>();
        if (nbt.contains("AttachedBlocks")) {
            var blocks = nbt.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                for (int i = 0; i < blocks.size(); i += 3) {
                    this.attachedBlocks.add(new BlockVector3(
                            blocks.get(i).data,
                            blocks.get(i + 1).data,
                            blocks.get(i + 2).data
                    ));
                }
            }
        } else nbt.putList("AttachedBlocks", new ListTag<>());

        // If the chunk was unloaded mid-move, resume ticking so the movement can complete.
        if (this.state == 1 || this.state == 3 || (this.progress > 0f && this.progress < 1f)) {
            this.scheduleUpdate();
        }
    }

    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte("State", this.state);
        this.nbt.putByte("NewState", this.newState);
        this.nbt.putFloat("Progress", this.progress);
        this.nbt.putFloat("LastProgress", this.lastProgress);
        this.nbt.putBoolean("powered", this.powered);
        this.nbt.putBoolean("hasPendingPower", this.hasPendingPower);
        this.nbt.putBoolean("pendingPowered", this.pendingPowered);
        this.nbt.putList("AttachedBlocks", getAttachedBlocks());
        this.nbt.putInt("facing", this.facing.getIndex());
        this.nbt.putBoolean("Sticky", this.sticky);
        this.nbt.putBoolean("Extending", this.extending);
    }

    @Override
    public boolean isBlockEntityValid() {
        var blockId = getBlock().getId();
        return blockId.equals(BlockID.PISTON) || blockId.equals(BlockID.STICKY_PISTON);
    }

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

    protected ListTag<IntTag> getAttachedBlocks() {
        var attachedBlocks = new ListTag<IntTag>();
        for (var block : this.attachedBlocks) {
            attachedBlocks.add(new IntTag(block.x));
            attachedBlocks.add(new IntTag(block.y));
            attachedBlocks.add(new IntTag(block.z));
        }
        return attachedBlocks;
    }

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