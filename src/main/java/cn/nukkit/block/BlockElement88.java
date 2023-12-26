package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement88 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_88");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement88() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement88(BlockState blockstate) {
        super(blockstate);
    }
}