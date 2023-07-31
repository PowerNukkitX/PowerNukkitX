package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.BlockRedstoneComparator;

/**
 * @author CreeperFace
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockRedstoneComparatorPowered extends BlockRedstoneComparator {

    public BlockRedstoneComparatorPowered() {
        this(0);
    }

    public BlockRedstoneComparatorPowered(int meta) {
        super(meta);
        this.isPowered = true;
    }

    @Override
    public int getId() {
        return POWERED_COMPARATOR;
    }

    @Override
    public String getName() {
        return "Comparator Block Powered";
    }

    @Override
    protected BlockRedstoneComparator getPowered() {
        return this;
    }
}
