package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockRedCandleCake extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_candle_cake", CommonBlockProperties.LIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedCandleCake() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedCandleCake(BlockState blockstate) {
        super(blockstate);
    }
}