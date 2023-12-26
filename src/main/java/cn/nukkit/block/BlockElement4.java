package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement4 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_4");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement4() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement4(BlockState blockstate) {
        super(blockstate);
    }
}