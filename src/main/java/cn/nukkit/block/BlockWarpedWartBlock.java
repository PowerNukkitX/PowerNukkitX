package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockWarpedWartBlock extends BlockSolid {


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
        return ItemTool.TYPE_HANDS_ONLY; //TODO Correct type is hoe
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
