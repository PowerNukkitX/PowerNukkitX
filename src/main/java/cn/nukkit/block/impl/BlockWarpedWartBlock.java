package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.item.ItemTool;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockWarpedWartBlock extends BlockSolid {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockWarpedWartBlock() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Warped Wart Block";
    }

    @Override
    public int getId() {
        return WARPED_WART_BLOCK;
    }

    // TODO Fix it in https://github.com/PowerNukkit/PowerNukkit/pull/370, the same for BlockNetherWartBlock
    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY; // TODO Correct type is hoe
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1;
    }
}
