package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockMangroveRoots extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(MANGROVE_ROOTS);
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMangroveRoots() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMangroveRoots(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Mangrove Roots";
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 0.7;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        return level.setBlock(this, this);
    }
}
