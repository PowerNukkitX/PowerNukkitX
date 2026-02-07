package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockWarpedWartBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_WART_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedWartBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedWartBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Wart Block";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
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
