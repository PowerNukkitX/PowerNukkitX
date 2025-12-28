package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement102 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_102");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement102() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement102(BlockState blockstate) {
        super(blockstate);
    }
}