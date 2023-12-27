package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeCandle extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_candle", CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeCandle(BlockState blockstate) {
        super(blockstate);
    }
}