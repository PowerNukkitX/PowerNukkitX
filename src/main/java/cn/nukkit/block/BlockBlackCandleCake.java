package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlackCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}