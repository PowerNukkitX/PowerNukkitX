package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGraniteStairs extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:granite_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGraniteStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGraniteStairs(BlockState blockstate) {
        super(blockstate);
    }
}