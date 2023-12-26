package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeCandle(BlockState blockstate) {
        super(blockstate);
    }
}