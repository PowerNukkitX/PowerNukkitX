package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}