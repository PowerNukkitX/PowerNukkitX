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

    public byte newState;

    public List<BlockVector3> attachedBlocks;
    public List<BlockVector3> breakBlocks;
    public boolean powered;
    public boolean hasPendingPower;
    public boolean pendingPowered;
    public float progress;
    public float lastProgress;
    private boolean pistonMovable = true;

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
        if (!this.markEntityAffected(entity) || this.movedEntitiesThisTick.contains(entity.runtimeId()))
            return false;
        EntityMoveByPistonEvent event = new EntityMoveByPistonEvent(entity, entity.getPosition());
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;
        entity.onPushByPiston(this);
        if (entity.closed)
            return false;
        this.movedEntitiesThisTick.add(entity.runtimeId());
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
        return this.affectedEntitiesThisTick.add(entity.runtimeId());
    }

    /**
     * Performs the preparatory operations before a move.
     * This method initializes the state prior to movement, including setting whether the structure is extending or contracting,
     * progress, state, and updates relevant moving data.
     *
     * @param extending      A boolean indicating whether is extending
     * @param attachedBlocks A list of BlockVector3 representing the blocks attached to the moving block.
     */
    public void preMove(boolean extending, List<BlockVector3> attachedBlocks, List<BlockVector3> breakBlocks) {
        this.finished = false;
        this.extending = extending;
        this.lastProgress = this.progress = extending ? 0 : 1;
        this.state = this.newState = (byte) (extending ? 1 : 3);
        this.attachedBlocks = attachedBlocks;
        this.breakBlocks = breakBlocks;
        this.pistonMovable = false;
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
            this.pistonMovable = true;
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
        this.breakBlocks.clear();
        this.finished = true;
        updateMovingData(false);
    }

    @Override
    public boolean isMovable() {
        return this.pistonMovable;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();

        this.state = this.nbt.getByte("State");
        this.newState = this.nbt.containsNumber("NewState") ? this.nbt.getByte("NewState") : this.state;
        this.progress = this.nbt.containsNumber("Progress") ? this.nbt.getFloat("Progress") : this.state == 2 ? 1.0f : 0.0f;
        this.lastProgress = this.nbt.containsNumber("LastProgress") ? this.nbt.getFloat("LastProgress") : this.state == 2 || this.state == 3 ? 1.0f : 0.0f;
        this.sticky = this.nbt.getBoolean("Sticky");
        this.pistonMovable = this.nbt.containsNumber("isMovable") ? this.nbt.getBoolean("isMovable") : this.state == 0;

        this.extending = this.state == 1;
        this.powered = this.state == 1 || this.state == 2;
        this.hasPendingPower = false;
        this.pendingPowered = false;

        Block block = this.getLevelBlock();
        this.facing = block instanceof Faceable faceable ? faceable.getBlockFace() : BlockFace.NORTH;

        this.attachedBlocks = readBlocks("AttachedBlocks");
        this.breakBlocks = readBlocks("BreakBlocks");

        this.finished = this.state != 1 && this.state != 3;
        if (!this.finished) {
            this.scheduleUpdate();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putList("AttachedBlocks", getAttachedBlocks())
                .putList("BreakBlocks", getBreakBlocks())
                .putFloat("LastProgress", this.lastProgress)
                .putByte("NewState", this.newState)
                .putFloat("Progress", this.progress)
                .putByte("State", this.state)
                .putBoolean("Sticky", this.sticky)
                .putBoolean("isMovable", this.pistonMovable);
    }

    @Override
    public boolean isBlockEntityValid() {
        var blockId = getBlock().getId();
        return blockId.equals(BlockID.PISTON) || blockId.equals(BlockID.STICKY_PISTON);
    }

    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putBoolean("isMovable", this.pistonMovable)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putList("AttachedBlocks", getAttachedBlocks())
                .putList("BreakBlocks", getBreakBlocks())
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);
    }

    protected ListTag<IntTag> getAttachedBlocks() {
        return writeBlocks(this.attachedBlocks);
    }

    protected ListTag<IntTag> getBreakBlocks() {
        return writeBlocks(this.breakBlocks);
    }

    private ListTag<IntTag> writeBlocks(List<BlockVector3> blocks) {
        var list = new ListTag<IntTag>();

        if (blocks != null) {
            for (var block : blocks) {
                list.add(new IntTag(block.x));
                list.add(new IntTag(block.y));
                list.add(new IntTag(block.z));
            }
        }

        return list;
    }

    private List<BlockVector3> readBlocks(String name) {
        var result = new ObjectArrayList<BlockVector3>();

        if (!this.nbt.containsList(name)) {
            return result;
        }

        var blocks = this.nbt.getList(name, IntTag.class);
        for (int i = 0; i + 2 < blocks.size(); i += 3) {
            result.add(new BlockVector3(
                    blocks.get(i).data,
                    blocks.get(i + 1).data,
                    blocks.get(i + 2).data
            ));
        }

        return result;
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
