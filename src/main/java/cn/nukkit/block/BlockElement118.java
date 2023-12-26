package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement118 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_118");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement118() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement118(BlockState blockstate) {
        super(blockstate);
    }
}