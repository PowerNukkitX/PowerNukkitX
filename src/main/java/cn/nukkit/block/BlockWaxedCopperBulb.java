package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_BULB, CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}