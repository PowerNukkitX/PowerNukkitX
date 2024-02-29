package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleCandle extends BlockCandle {
    public static final BlockProperties PROPERTIES = new BlockProperties(PURPLE_CANDLE, CommonBlockProperties.CANDLES, CommonBlockProperties.LIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleCandle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public Block toCakeForm() {
        return new BlockPurpleCandleCake();
    }
}