package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownCandle(BlockState blockstate) {
        super(blockstate);
    }
}