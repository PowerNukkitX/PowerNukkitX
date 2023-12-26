package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCandle(BlockState blockstate) {
        super(blockstate);
    }
}