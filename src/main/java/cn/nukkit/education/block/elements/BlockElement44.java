package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement44 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_44");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement44() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement44(BlockState blockstate) {
        super(blockstate);
    }
}