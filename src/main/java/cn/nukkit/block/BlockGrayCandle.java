package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGrayCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayCandle(BlockState blockstate) {
        super(blockstate);
    }
}