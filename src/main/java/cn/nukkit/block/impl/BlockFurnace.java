package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.block.BlockID;

/**
 * @author Angelic47 (Nukkit Project)
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockFurnace extends BlockFurnaceBurning {

    public BlockFurnace() {
        this(0);
    }

    public BlockFurnace(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getId() {
        return BlockID.FURNACE;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
