package cn.nukkit.blockentity;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.tag.CompoundTag;

import javax.annotation.Nullable;

/**
 * @author CreeperFace
 * @since 11.4.2017
 */
public class BlockEntityMovingBlock extends BlockEntitySpawnable {

    @PowerNukkitOnly
    protected String blockString;
    protected Block block;

    protected BlockVector3 piston;

    public BlockEntityMovingBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Since("1.19.60-r1")
    @Override
    public void loadNBT() {
        super.loadNBT();
        if (namedTag.contains("movingBlock")) {
            CompoundTag blockData = namedTag.getCompound("movingBlock");

            this.blockString = blockData.getString("name");
            this.block = Block.get(blockData.getInt("id"), blockData.getInt("meta"));
        } else {
            this.close();
        }

        if (namedTag.contains("pistonPosX") && namedTag.contains("pistonPosY") && namedTag.contains("pistonPosZ")) {
            this.piston = new BlockVector3(namedTag.getInt("pistonPosX"), namedTag.getInt("pistonPosY"), namedTag.getInt("pistonPosZ"));
        } else {
            this.piston = new BlockVector3(0, -1, 0);
        }
    }

    @PowerNukkitOnly
    @Deprecated @DeprecationDetails(by = "PowerNukkit", since = "1.4.0.0-PN", reason = "renamed", replaceWith = "getMovingBlockEntityCompound()")
    public CompoundTag getBlockEntity() {
        return getMovingBlockEntityCompound();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nullable
    public CompoundTag getMovingBlockEntityCompound() {
        if (this.namedTag.contains("movingEntity")) {
            return this.namedTag.getCompound("movingEntity");
        }

        return null;
    }

    @PowerNukkitOnly
    public Block getMovingBlock() {
        return this.block;
    }

    @PowerNukkitOnly
    public String getMovingBlockString() {
        return this.blockString;
    }

    @PowerNukkitOnly
    public void moveCollidedEntities(BlockEntityPistonArm piston, BlockFace moveDirection) {
        AxisAlignedBB bb = block.getBoundingBox();

        if (bb == null) {
            return;
        }

        var horizontal = moveDirection != BlockFace.UP && moveDirection != BlockFace.DOWN;

        //TODO: 修复移动
        bb = bb.getOffsetBoundingBox(
                this.x + moveDirection.getXOffset() * (piston.extending && horizontal ? 1 : -2),
                this.y + moveDirection.getYOffset() * (piston.extending ? 1 : -2),
                this.z + moveDirection.getZOffset() * (piston.extending && horizontal ? 1 : -2)
        );

        Entity[] entities = this.level.getCollidingEntities(bb);

        for (Entity entity : entities) {
            piston.moveEntity(entity, moveDirection);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == BlockID.MOVING_BLOCK;
    }
}
