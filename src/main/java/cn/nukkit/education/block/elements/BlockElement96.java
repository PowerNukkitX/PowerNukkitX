package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement96 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_96");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement96() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement96(BlockState blockstate) {
        super(blockstate);
    }
}