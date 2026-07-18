package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.BlockFace.Plane;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class BlockTrappedChest extends BlockChest {

    public static final BlockProperties PROPERTIES = new BlockProperties(TRAPPED_CHEST, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTrappedChest() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTrappedChest(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Trapped Chest";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? BlockFace.fromHorizontalIndex(player.getDirection().getOpposite().getHorizontalIndex()) : BlockFace.SOUTH);

        BlockEntityChest chest = null;

        for (BlockFace side : Plane.HORIZONTAL) {
            if ((getBlockFace() == BlockFace.WEST || getBlockFace() == BlockFace.EAST) && (side == BlockFace.WEST || side == BlockFace.EAST)) {
                continue;
            } else if ((getBlockFace() == BlockFace.NORTH || getBlockFace() == BlockFace.SOUTH) && (side == BlockFace.NORTH || side == BlockFace.SOUTH)) {
                continue;
            }
            Block c = this.getSide(side);
            if (c instanceof BlockTrappedChest trappedChest && trappedChest.getBlockFace() == getBlockFace()) {
                BlockEntity blockEntity = this.getLevel().getBlockEntity(trappedChest);
                if (blockEntity instanceof BlockEntityChest && !((BlockEntityChest) blockEntity).isPaired()) {
                    chest = (BlockEntityChest) blockEntity;
                    break;
                }
            }
        }

        this.getLevel().setBlock(block, this, true, true);
        CompoundTag nbt = new CompoundTag()
                .putList("Items", new ListTag<CompoundTag>())
                .putString("id", BlockEntity.CHEST)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        if (item.hasCustomName()) {
            nbt.putString("CustomName", item.getCustomName());
        }

        if (item.hasCustomBlockData()) {
            nbt.putAll(item.getCustomBlockData());
        }

        BlockEntityChest blockEntity = (BlockEntityChest) BlockEntity.createBlockEntity(BlockEntity.CHEST, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);

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
    public int getWeakPower(BlockFace face) {
        int playerCount = 0;

        BlockEntity blockEntity = this.level.getBlockEntity(this);

        if (blockEntity instanceof BlockEntityChest) {
            playerCount = ((BlockEntityChest) blockEntity).getInventory().getViewers().size();
        }

        return Math.min(playerCount, 15);
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return side == BlockFace.UP ? this.getWeakPower(side) : 0;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }
}
