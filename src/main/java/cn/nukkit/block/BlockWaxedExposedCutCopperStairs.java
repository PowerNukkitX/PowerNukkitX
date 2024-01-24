package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCutCopperStairs extends BlockExposedCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_CUT_COPPER_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}