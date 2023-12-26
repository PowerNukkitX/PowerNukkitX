package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement70 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_70");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement70() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement70(BlockState blockstate) {
        super(blockstate);
    }
}