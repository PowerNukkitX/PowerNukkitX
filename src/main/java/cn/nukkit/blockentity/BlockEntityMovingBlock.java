package cn.nukkit.blockentity;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
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
        var bb = block.getBoundingBox();
        if (bb == null)
            return;
        bb = bb.getOffsetBoundingBox(
                this.x + (piston.progress * moveDirection.getXOffset()) - moveDirection.getXOffset(),
                this.y + (piston.progress * moveDirection.getYOffset()) - moveDirection.getYOffset(),
                this.z + (piston.progress * moveDirection.getZOffset()) - moveDirection.getZOffset()
                //带动站在移动方块上的实体
        ).addCoord(0, moveDirection.getAxis().isHorizontal() ? 0.25 : 0, 0);
        for (Entity entity : this.level.getCollidingEntities(bb))
            piston.moveEntity(entity, moveDirection);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == BlockID.MOVING_BLOCK;
    }
}
