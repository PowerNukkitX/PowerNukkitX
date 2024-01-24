package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopperSlab extends BlockExposedCutCopperSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCutCopperSlab(BlockState blockstate) {
        super(blockstate, WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}