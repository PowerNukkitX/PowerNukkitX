package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_BULB, CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}