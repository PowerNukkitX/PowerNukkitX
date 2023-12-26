package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement7 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_7");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement7() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement7(BlockState blockstate) {
        super(blockstate);
    }
}