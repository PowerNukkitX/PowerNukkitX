package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement110 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_110");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement110() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement110(BlockState blockstate) {
        super(blockstate);
    }
}