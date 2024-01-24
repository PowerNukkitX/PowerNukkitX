package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockNetherWartBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_WART_BLOCK);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherWartBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherWartBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Nether Wart Block";
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
