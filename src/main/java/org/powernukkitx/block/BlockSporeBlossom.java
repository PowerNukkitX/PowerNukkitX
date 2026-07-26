package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


public class BlockSporeBlossom extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SPORE_BLOSSOM);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSporeBlossom() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSporeBlossom(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Spore Blossom";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (target.isSolid() && face == BlockFace.DOWN) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }
}
