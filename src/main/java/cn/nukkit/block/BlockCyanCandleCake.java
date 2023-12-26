package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}