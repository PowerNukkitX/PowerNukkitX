package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
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
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;

import java.util.ArrayList;
import java.util.List;


/**
 * @author CreeperFace
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

    public BlockEntityPistonArm(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

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
                // Moves entities standing on moving blocks
        ).addCoord(0, pushDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);
        for (var entity : this.level.getCollidingEntities(bb))
            moveEntity(entity, pushDirection);
    }

    void moveEntity(Entity entity, BlockFace moveDirection) {
        // No downward force is required
        if (moveDirection == BlockFace.DOWN)
            return;
        var diff = Math.abs(this.progress - this.lastProgress);
        // Player clients automatically handle movement
        if (diff == 0 || !entity.canBePushedByPiston() || entity instanceof Player)
            return;
        EntityMoveByPistonEvent event = new EntityMoveByPistonEvent(entity, entity.getPosition());
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        entity.onPushByPiston(this);
        if (entity.closed)
            return;
        // Need to counteract gravity
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

    /**
     * The piston extension process lasts 2gt.
     */
    @Override
    public boolean onUpdate() {

        // This bool marks whether the next gt needs to continue updating
        var hasUpdate = true;
        // Promotion process
        if (this.extending) {
            this.progress = Math.min(1, this.progress + MOVE_STEP);
            this.lastProgress = Math.min(1, this.lastProgress + MOVE_STEP);
        } else {
            this.progress = Math.max(0, this.progress - MOVE_STEP);
            this.lastProgress = Math.max(0, this.lastProgress - MOVE_STEP);
        }
        moveCollidedEntities();
        if (this.progress == this.lastProgress) {
            // End Push
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
                    var movedBlockEntity = movingBlockBlockEntity.getMovingBlockEntityCompound().toBuilder();
                    if (moved instanceof BlockEntityHolder<?> holder && movedBlockEntity != null) {
                        movedBlockEntity.putInt("x", movingBlock.getFloorX());
                        movedBlockEntity.putInt("y", movingBlock.getFloorY());
                        movedBlockEntity.putInt("z", movingBlock.getFloorZ());
                        BlockEntityHolder.setBlockAndCreateEntity(holder, false, true, movedBlockEntity.build());
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
            hasUpdate = false;
            updateMovingData(false);
        }
        return super.onUpdate() || hasUpdate;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        this.state = this.namedTag.getByte("State");
        this.newState = this.namedTag.getByte("NewState");
        if (namedTag.containsKey("Progress"))
            this.progress = namedTag.getFloat("Progress");
        if (namedTag.containsKey("LastProgress"))
            this.lastProgress = namedTag.getFloat("LastProgress");
        this.sticky = namedTag.getBoolean("Sticky");
        this.extending = namedTag.getBoolean("Extending");
        this.powered = namedTag.getBoolean("powered");
        if (namedTag.containsKey("facing")) {
            this.facing = BlockFace.fromIndex(namedTag.getInt("facing"));
        } else {
            var block = this.getLevelBlock();
            if (block instanceof Faceable faceable)
                this.facing = faceable.getBlockFace();
            else
                this.facing = BlockFace.NORTH;
        }
        attachedBlocks = new ObjectArrayList<>();
        if (namedTag.containsKey("AttachedBlocks")) {
            var blocks = namedTag.getList("AttachedBlocks", NbtType.INT);
            if (blocks != null && blocks.size() > 0) {
                for (int i = 0; i < blocks.size(); i += 3) {
                    this.attachedBlocks.add(new BlockVector3(
                            blocks.get(i),
                            blocks.get(i + 1),
                            blocks.get(i + 2)
                    ));
                }
            }
        } else this.namedTag = namedTag.toBuilder().putList("AttachedBlocks", NbtType.INT, new IntArrayList()).build();

        // If the chunk was unloaded mid-move, resume ticking so the movement can complete.
        if (this.state == 1 || this.state == 3 || (this.progress > 0f && this.progress < 1f)) {
            this.scheduleUpdate();
        }
    }

    public void saveNBT() {
        super.saveNBT();
        this.namedTag = this.namedTag.toBuilder()
                .putByte("State", this.state)
                .putByte("NewState", this.newState)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putBoolean("powered", this.powered)
                .putList("AttachedBlocks", NbtType.INT, getAttachedBlocks())
                .putInt("facing", this.facing.getIndex())
                .putBoolean("Sticky", this.sticky)
                .putBoolean("Extending", this.extending)
                .build();
    }

    @Override
    public boolean isBlockEntityValid() {
        var blockId = getBlock().getId();
        return blockId.equals(BlockID.PISTON) || blockId.equals(BlockID.STICKY_PISTON);
    }

    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.movable)
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putList("AttachedBlocks", NbtType.INT, getAttachedBlocks())
                .putList("BreakBlocks", NbtType.INT, new IntArrayList())
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState)
                .build();
    }

    protected List<Integer> getAttachedBlocks() {
        var attachedBlocks = new IntArrayList();
        for (var block : this.attachedBlocks) {
            attachedBlocks.add(block.x);
            attachedBlocks.add(block.y);
            attachedBlocks.add(block.z);
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