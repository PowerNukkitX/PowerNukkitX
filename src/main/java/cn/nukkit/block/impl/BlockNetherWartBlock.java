package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.item.ItemTool;

@Since("1.4.0.0-PN")
public class BlockNetherWartBlock extends BlockSolid {

    @Since("1.4.0.0-PN")
    public BlockNetherWartBlock() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
    }

    @Override
    public int getId() {
        return BLOCK_NETHER_WART_BLOCK;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @PowerNukkitDifference(info = "It's now hoe instead of none", since = "1.4.0.0-PN")
    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }
}
