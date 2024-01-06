package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCutCopperStairs extends BlockWeatheredCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_WEATHERED_CUT_COPPER_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}