package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement71 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_71");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement71() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement71(BlockState blockstate) {
        super(blockstate);
    }
}