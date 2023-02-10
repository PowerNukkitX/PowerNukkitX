package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockPistonHead;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityMoveByPistonEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
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
 * @author CreeperFace
 */
@PowerNukkitXDifference(info = "活塞速度现在匹配原版")
@Since("1.19.60-r1")
public class BlockEntityPistonArm extends BlockEntitySpawnable {
    @PowerNukkitOnly
    public static final float MOVE_STEP = Utils.dynamic(0.25f);

    public BlockFace facing;
    public boolean extending;
    public boolean sticky;
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public byte state;
    @PowerNukkitXOnly
    @Since("1.19.60-r1")
    public byte newState = 1;
    @PowerNukkitOnly
    public List<BlockVector3> attachedBlocks;
    public boolean powered;
    public float progress;
    public float lastProgress = 1;
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean finished = true;

    public BlockEntityPistonArm(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void moveCollidedEntities() {
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
                //带动站在移动方块上的实体
        ).addCoord(0, pushDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);;
        for (var entity : this.level.getCollidingEntities(bb))
            moveEntity(entity, pushDirection);
    }

    void moveEntity(Entity entity, BlockFace moveDirection) {
        //不需要给予向下的力
        if (moveDirection == BlockFace.DOWN)
            return;
        var diff = Math.abs(this.progress - this.lastProgress);
        //玩家客户端会自动处理移动
        if (diff == 0 || !entity.canBePushed() || entity instanceof Player)
            return;
        EntityMoveByPistonEvent event = new EntityMoveByPistonEvent(entity, entity.getPosition());
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        entity.onPushByPiston(this);
        if (entity.closed)
            return;
        //需要抵消重力
        entity.move(
                diff * moveDirection.getXOffset(),
                diff * moveDirection.getYOffset() * (moveDirection == BlockFace.UP ? 2 : 1),
                diff * moveDirection.getZOffset()
        );
    }

    public void preMove(boolean extending, List<BlockVector3> attachedBlocks) {
        this.finished = false;
        this.extending = extending;
        this.lastProgress = this.progress = extending ? 0 : 1;
        this.state = this.newState = (byte) (extending ? 1 : 3);
        this.attachedBlocks = attachedBlocks;
        this.movable = false;
        //必须先于MOVING_BLOCK发包过去，所以immediately=true
        updateMovingData(true);
    }

    //需要先调用preMove
    public void move() {
        //开始推动
        this.lastProgress = this.extending ? -MOVE_STEP : 1 + MOVE_STEP;
        this.moveCollidedEntities();
        this.scheduleUpdate();
    }

    /**
     * 活塞伸出过程持续2gt
     */
    @Override
    public boolean onUpdate() {
        //此bool标记下一gt是否需要继续更新
        var hasUpdate = true;
        //推动过程
        if (this.extending) {
            this.progress = Math.min(1, this.progress + MOVE_STEP);
            this.lastProgress = Math.min(1, this.lastProgress + MOVE_STEP);
        } else {
            this.progress = Math.max(0, this.progress - MOVE_STEP);
            this.lastProgress = Math.max(0, this.lastProgress - MOVE_STEP);
        }
        moveCollidedEntities();
        if (this.progress == this.lastProgress) {
            //结束推动
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
                    //普通方块更新
                    this.level.setBlock(movingBlock, moved, true, true);
                    var movedBlockEntity = movingBlockBlockEntity.getMovingBlockEntityCompound();
                    if (movedBlockEntity != null) {
                        movedBlockEntity.putInt("x", movingBlock.getFloorX());
                        movedBlockEntity.putInt("y", movingBlock.getFloorY());
                        movedBlockEntity.putInt("z", movingBlock.getFloorZ());
                        BlockEntity.createBlockEntity(movedBlockEntity.getString("id"), this.level.getChunk(movingBlock.getChunkX(), movingBlock.getChunkZ()), movedBlockEntity);
                    }
                    //活塞更新
                    moved.onUpdate(Level.BLOCK_UPDATE_MOVED);
                }
            }
            for (var update : redstoneUpdateList)
                //红石更新
                RedstoneComponent.updateAllAroundRedstone(new Position(update.x, update.y, update.z, this.level));
            var pos = getSide(facing);
            if (!extending) {
                //未伸出的活塞可以被推动
                this.movable = true;
                if (this.level.getBlock(pos) instanceof BlockPistonHead) {
                    this.level.setBlock(pos, 1, Block.get(Block.AIR), true, false);
                    //方块更新
                    this.level.setBlock(pos, Block.get(Block.AIR), true);
                }
            }
            //对和活塞直接接触的观察者进行更新
            this.level.updateAroundObserver(this);
            //下一计划刻再自检一遍，防止出错
            this.level.scheduleUpdate(this.getLevelBlock(), 1);
            this.attachedBlocks.clear();
            this.finished = true;
            hasUpdate = false;
            updateMovingData(false);
        }
        return super.onUpdate() || hasUpdate;
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        this.state = (byte) this.namedTag.getByte("State");
        this.newState = (byte) this.namedTag.getByte("NewState");
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
        } else namedTag.putList(new ListTag<>("AttachedBlocks"));
    }

    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("State", this.state);
        this.namedTag.putByte("NewState", this.newState);
        this.namedTag.putFloat("Progress", this.progress);
        this.namedTag.putFloat("LastProgress", this.lastProgress);
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
                .putFloat("Progress", this.progress)
                .putFloat("LastProgress", this.lastProgress)
                .putBoolean("isMovable", this.movable)
                .putList(getAttachedBlocks())
                .putList(new ListTag<>("BreakBlocks"))
                .putBoolean("Sticky", this.sticky)
                .putByte("State", this.state)
                .putByte("NewState", this.newState);
    }

    protected ListTag<IntTag> getAttachedBlocks() {
        var attachedBlocks = new ListTag<IntTag>("AttachedBlocks");
        for (var block : this.attachedBlocks) {
            attachedBlocks.add(new IntTag("", block.x));
            attachedBlocks.add(new IntTag("", block.y));
            attachedBlocks.add(new IntTag("", block.z));
        }
        return attachedBlocks;
    }

    public void updateMovingData(boolean immediately) {
        var packet = this.getSpawnPacket();
        if (!immediately) {
            if (packet != null)
                this.level.addChunkPacket(getChunkX(), getChunkZ(), packet);
        } else {
            Server.broadcastPacket(this.getLevel().getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values(), packet);
        }
    }
}
