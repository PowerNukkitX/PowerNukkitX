package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHoneycombBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(HONEYCOMB_BLOCK);

    public BlockHoneycombBlock() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHoneycombBlock(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HANDS_ONLY;
    }

    @Override
    public boolean canHarvestWithHand() {
        return true;
    }

    @Override
    public String getName() {
        return "Honeycomb Block";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

}
