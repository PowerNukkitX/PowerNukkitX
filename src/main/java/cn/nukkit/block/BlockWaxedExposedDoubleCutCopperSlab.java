package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedDoubleCutCopperSlab extends BlockExposedDoubleCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockWaxedExposedCutCopperSlab.PROPERTIES.getDefaultState();
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}