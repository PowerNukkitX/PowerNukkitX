package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement52 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_52");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement52() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement52(BlockState blockstate) {
        super(blockstate);
    }
}