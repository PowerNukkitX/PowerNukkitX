package cn.nukkit.education.block;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement74 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_74");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement74() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement74(BlockState blockstate) {
        super(blockstate);
    }
}