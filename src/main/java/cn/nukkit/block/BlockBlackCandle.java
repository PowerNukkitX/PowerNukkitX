package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackCandle(BlockState blockstate) {
        super(blockstate);
    }
}