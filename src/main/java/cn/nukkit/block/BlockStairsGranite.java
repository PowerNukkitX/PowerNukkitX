package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.ItemTool;


public class BlockStairsGranite extends BlockStairs {

    public BlockStairsGranite() {
        this(0);
    }


    public BlockStairsGranite(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return GRANITE_STAIRS;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 30;
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
    public String getName() {
        return "Granite Stairs";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
