package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:copper_bulb", CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}