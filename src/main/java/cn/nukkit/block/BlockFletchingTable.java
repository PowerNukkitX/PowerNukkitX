package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockFletchingTable extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(FLETCHING_TABLE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFletchingTable() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFletchingTable(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Fletching Table";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }
}
