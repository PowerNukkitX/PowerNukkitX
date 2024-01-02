package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_COPPER_BULB, CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}