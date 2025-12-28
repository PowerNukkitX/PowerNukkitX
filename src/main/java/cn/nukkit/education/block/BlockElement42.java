package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement42 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_42");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement42() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement42(BlockState blockstate) {
        super(blockstate);
    }
}