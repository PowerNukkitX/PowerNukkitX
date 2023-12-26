package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGreenCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenCandle(BlockState blockstate) {
        super(blockstate);
    }
}