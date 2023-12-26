package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCandle(BlockState blockstate) {
        super(blockstate);
    }
}