package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedDoubleCutCopperSlab extends BlockDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockWaxedCutCopperSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}