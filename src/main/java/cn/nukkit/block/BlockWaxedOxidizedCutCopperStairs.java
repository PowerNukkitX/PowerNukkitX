package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedOxidizedCutCopperStairs extends BlockOxidizedCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_OXIDIZED_CUT_COPPER_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedOxidizedCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedOxidizedCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isWaxed() {
        return true;
    }
}