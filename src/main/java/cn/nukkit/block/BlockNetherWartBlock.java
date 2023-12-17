package cn.nukkit.block;

import cn.nukkit.item.ItemTool;


public class BlockNetherWartBlock extends BlockSolid {


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


    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

}
