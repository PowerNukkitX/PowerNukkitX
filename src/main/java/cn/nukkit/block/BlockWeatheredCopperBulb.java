package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:weathered_copper_bulb", CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeatheredCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeatheredCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}