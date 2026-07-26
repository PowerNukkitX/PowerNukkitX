package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockJigsaw extends BlockSolid implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(JIGSAW, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.ROTATION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockJigsaw() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockJigsaw(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Jigsaw";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(CommonBlockProperties.FACING_DIRECTION));
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(CommonBlockProperties.FACING_DIRECTION, face.getIndex());
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (Math.abs(player.x - this.x) < 2 && Math.abs(player.z - this.z) < 2) {
            double y = player.y + player.getEyeHeight();

            if (y - this.y > 2) {
                this.setBlockFace(BlockFace.UP);
            } else if (this.y - y > 0) {
                this.setBlockFace(BlockFace.DOWN);
            } else {
                this.setBlockFace(player.getHorizontalFacing().getOpposite());
            }
        } else {
            this.setBlockFace(player.getHorizontalFacing().getOpposite());
        }
        this.level.setBlock(block, this, true, false);

        return super.place(item, block, target, face, fx, fy, fz, player);
    }
}
