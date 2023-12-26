package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGrayCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}