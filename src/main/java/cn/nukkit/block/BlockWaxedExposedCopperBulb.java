package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopperBulb extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:waxed_exposed_copper_bulb", CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopperBulb(BlockState blockstate) {
        super(blockstate);
    }
}