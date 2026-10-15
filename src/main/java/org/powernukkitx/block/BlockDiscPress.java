package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.CommonPropertyMap;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.utils.Faceable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Added in 1.26.60 without a creative tab entry or a use of its own yet, so it only faces the
 * player who placed it.
 *
 * @author xRookieFight
 * @since 15/10/2026
 */
public class BlockDiscPress extends BlockSolid implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(DISC_PRESS, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDiscPress() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDiscPress(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Disc Press";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex > -1) {
            this.setPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION,
                CommonPropertyMap.CARDINAL_BLOCKFACE.inverse().get(BlockFace.fromHorizontalIndex(horizontalIndex)));
        }
    }

    @Override
    public BlockFace getBlockFace() {
        return CommonPropertyMap.CARDINAL_BLOCKFACE.get(getPropertyValue(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION));
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        return this.getLevel().setBlock(block, this, true, true);
    }
}
