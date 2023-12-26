package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}