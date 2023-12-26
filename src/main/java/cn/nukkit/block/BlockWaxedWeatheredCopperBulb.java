package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedWeatheredCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_weathered_copper_bulb", CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedWeatheredCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedWeatheredCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}