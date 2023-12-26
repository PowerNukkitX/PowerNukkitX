package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlueCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueCandle(BlockState blockstate) {
        super(blockstate);
    }
}