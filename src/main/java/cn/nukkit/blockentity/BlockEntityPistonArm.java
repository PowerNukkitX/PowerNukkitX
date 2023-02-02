package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPistonHead;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityMoveByPistonEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;

/**
 * @author CreeperFace
 */
@PowerNukkitXDifference(info = "活塞速度现在匹配原版")
@Since("1.19.60-r1")
public class BlockEntityPistonArm extends BlockEntitySpawnable {
    public BlockFace facing;
    public boolean extending;
    public boolean sticky;
    public int state;
    public int newState;
    public List<BlockVector3> attachedBlocks;
    public boolean powered;

    public BlockEntityPistonArm(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    private void moveCollidedEntities() {
        BlockFace pushDir = this.extending ? facing : facing.getOpposite();
        for (BlockVector3 pos : this.attachedBlocks) {
            BlockEntity blockEntity = this.level.getBlockEntity(pos.getSide(pushDir));

            if (blockEntity instanceof BlockEntityMovingBlock) {
                ((BlockEntityMovingBlock) blockEntity).moveCollidedEntities(this, pushDir);
            }
        }

        var horizontal = pushDir != BlockFace.UP && pushDir != BlockFace.DOWN;

        AxisAlignedBB bb = new SimpleAxisAlignedBB(0, 0, 0, 1, horizontal ? 2 : 1, 1).getOffsetBoundingBox(
                this.x + pushDir.getXOffset() * (this.extending && horizontal ? 1 : -2),
                this.y + pushDir.getYOffset() * (this.extending ? 1 : -2),
                this.z + pushDir.getZOffset() * (this.extending && horizontal ? 1 : -2)
        );

        Entity[] entities = this.level.getCollidingEntities(bb);

        for (Entity entity : entities) {
            moveEntity(entity, pushDir);
        }
    }

    void moveEntity(Entity entity, BlockFace moveDirection) {
        if (!entity.canBePushed()) {
            return;
        }

        EntityMoveByPistonEvent event = new EntityMoveByPistonEvent(entity, entity.getPosition());
        this.level.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        entity.onPushByPiston(this);

        if (!entity.closed) {
            if (entity instanceof Player player) {
                player.sendPosition(new Vector3(
                        player.x + moveDirection.getXOffset(),
                        player.y + moveDirection.getYOffset(),
                        player.z + moveDirection.getZOffset()
                ));
            } else {
                entity.move(
                        moveDirection.getXOffset(),
                        moveDirection.getYOffset(),
                        moveDirection.getZOffset()
                );
            }
        }
    }

    public void move(boolean extending, List<BlockVector3> attachedBlocks) {
        this.extending = extending;
        this.state = this.newState = extending ? 1 : 3;
        this.attachedBlocks = attachedBlocks;
        this.movable = false;

        BlockEntityDataPacket packet = this.getSpawnPacket();
        if (packet != null) {
            this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }
        this.moveCollidedEntities();
        this.scheduleUpdate();
    }

    /**
     * 活塞伸出过程持续2gt
     */
    @Override
    public boolean onUpdate() {
        this.state = this.newState = extending ? 2 : 0;

        BlockFace pushDir = this.extending ? facing : facing.getOpposite();

        for (BlockVector3 pos : this.attachedBlocks) {
            BlockEntity movingBlock = this.level.getBlockEntity(pos.getSide(pushDir));

            if (movingBlock instanceof BlockEntityMovingBlock movingBlockBlockEntity) {
                movingBlock.close();

                Block moved = movingBlockBlockEntity.getMovingBlock();
                moved.position(movingBlock);

                this.level.setBlock(movingBlock, 1, Block.get(BlockID.AIR), true, false);
                this.level.setBlock(movingBlock, moved, true, false);

                CompoundTag movedBlockEntity = movingBlockBlockEntity.getMovingBlockEntityCompound();
                if (movedBlockEntity != null) {
                    movedBlockEntity.putInt("x", movingBlock.getFloorX());
                    movedBlockEntity.putInt("y", movingBlock.getFloorY());
                    movedBlockEntity.putInt("z", movingBlock.getFloorZ());
                    BlockEntity.createBlockEntity(movedBlockEntity.getString("id"), this.level.getChunk(movingBlock.getChunkX(), movingBlock.getChunkZ()), movedBlockEntity);
                }

                //活塞更新
                moved.onUpdate(Level.BLOCK_UPDATE_MOVED);
                RedstoneComponent.updateAroundRedstone(moved);
                this.level.scheduleUpdate(moved, 1);
            }
        }

        Vector3 pos = getSide(facing);
        if (!extending) {
            this.movable = true;
            if (this.level.getBlock(pos) instanceof BlockPistonHead) {
                this.level.setBlock(pos, 1, Block.get(Block.AIR), true, false);
                this.level.setBlock(pos, Block.get(Block.AIR), true);
            }
        }

        this.level.scheduleUpdate(this.getLevelBlock(), 1);
        this.attachedBlocks.clear();

        BlockEntityDataPacket packet = this.getSpawnPacket();
        if (packet != null) {
            this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        }

        return false;
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        this.sticky = namedTag.getBoolean("Sticky");
        this.extending = namedTag.getBoolean("Extending");
        this.movable = !this.extending;
        this.powered = namedTag.getBoolean("powered");

        if (namedTag.contains("facing")) {
            this.facing = BlockFace.fromIndex(namedTag.getInt("facing"));
        } else {
            Block block = this.getLevelBlock();
            if (block instanceof Faceable) {
                this.facing = ((Faceable) block).getBlockFace();
            } else {
                this.facing = BlockFace.NORTH;
            }
        }

        attachedBlocks = new ObjectArrayList<>();

        if (namedTag.contains("AttachedBlocks")) {
            ListTag<IntTag> blocks = namedTag.getList("AttachedBlocks", IntTag.class);
            if (blocks != null && blocks.size() > 0) {
                for (int i = 0; i < blocks.size(); i += 3) {
                    this.attachedBlocks.add(new BlockVector3(
                            blocks.get(i).data,
                            blocks.get(i + 1).data,
                            blocks.get(i + 2).data
                    ));
                }
            }
        } else {
            namedTag.putList(new ListTag<>("AttachedBlocks"));
        }
    }

    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("State", this.state);
        this.namedTag.putByte("NewState", this.newState);
        this.namedTag.putBoolean("powered", this.powered);
        this.namedTag.putList(getAttachedBlocks());
        this.namedTag.putInt("facing", this.facing.getIndex());
        this.namedTag.putBoolean("Sticky", this.sticky);
        this.namedTag.putBoolean("Extending", this.extending);
    }

    @Override
    public boolean isBlockEntityValid() {
        var blockId = getBlock().getId();
        return blockId == BlockID.PISTON || blockId == BlockID.STICKY_PISTON;
    }

    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, PISTON_ARM)
                .putBoolean("isMovable", this.movable)
                .putList(getAttachedBlocks())
                .putList(new ListTag<>("BreakBlocks"))
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);
    }

    private ListTag<IntTag> getAttachedBlocks() {
        ListTag<IntTag> attachedBlocks = new ListTag<>("AttachedBlocks");
        for (BlockVector3 block : this.attachedBlocks) {
            attachedBlocks.add(new IntTag("", block.x));
            attachedBlocks.add(new IntTag("", block.y));
            attachedBlocks.add(new IntTag("", block.z));
        }

        return attachedBlocks;
    }

    @Override
    public boolean isMovable() {
        return super.isMovable();
    }
}
