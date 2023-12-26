package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
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