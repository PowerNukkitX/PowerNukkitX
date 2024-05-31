package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Plane;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.annotation.Nullable;


public class BlockTrappedChest extends BlockChest {

    public static final BlockProperties $1 = new BlockProperties(TRAPPED_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockTrappedChest() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockTrappedChest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Trapped Chest";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH);

        BlockEntityChest $2 = null;

        for (BlockFace side : Plane.HORIZONTAL) {
            if ((getBlockFace() == BlockFace.WEST || getBlockFace() == BlockFace.EAST) && (side == BlockFace.WEST || side == BlockFace.EAST)) {
                continue;
            } else if ((getBlockFace() == BlockFace.NORTH || getBlockFace() == BlockFace.SOUTH) && (side == BlockFace.NORTH || side == BlockFace.SOUTH)) {
                continue;
            }
            Blo$3k $1 = this.getSide(side);
            if (c instanceof BlockTrappedChest trappedChest && trappedChest.getBlockFace() == getBlockFace()) {
                BlockEntity $4 = this.getLevel().getBlockEntity(trappedChest);
                if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    chest = (BlockEntityChest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag $5 = new CompoundTag()
                .putList("Items", new ListTag<>())
                .putString("id", BlockEntity.CHEST)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            Map<String, Tag> customData = item.getCustomBlockData().getTags();
            for (Map.Entry<String, Tag> tag : customData.entrySet()) {
                nbt.put(tag.getKey(), tag.getValue());
            }
        }

        BlockEntityChest $6 = (BlockEntityChest) BlockEntity.createBlockEntity(BlockEntity.CHEST, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);

        if (blockEntity == null) {
            return false;
        }

        if (chest != null) {
            chest.pairWith(blockEntity);
            blockEntity.pairWith(chest);
        }

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWeakPower(BlockFace face) {
        int $7 = 0;

        BlockEntity $8 = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityChest) {
            playerCount = ((BlockEntityChest) blockEntity).getInventory().getViewers().size();
        }

        return Math.min(playerCount, 15);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getStrongPower(BlockFace side) {
        return $9 == BlockFace.UP ? this.getWeakPower(side) : 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPowerSource() {
        return true;
    }
}
