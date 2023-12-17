package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockNetheriteBlock extends BlockSolid {


    public BlockNetheriteBlock() {
    }

    @Override
    public int getId() {
        return NETHERITE_BLOCK;
    }

    @Override
    public String getName() {
        return "Netherite Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 35; // TODO Should be 50, but the break time is glitchy (same with obsidian but less noticeable because of the texture)
    }

    @Override
    public double getResistance() {
        return 6000;
    }


    @Override
    public int getToolTier() {
        return ItemTool.TIER_DIAMOND;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }


    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
