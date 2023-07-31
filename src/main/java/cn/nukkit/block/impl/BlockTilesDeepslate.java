package cn.nukkit.block.impl;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSolid;
import cn.nukkit.item.ItemTool;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockTilesDeepslate extends BlockSolid {

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockTilesDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_TILES;
    }

    @Override
    public String getName() {
        return "Deepslate Tiles";
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}
