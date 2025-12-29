package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement11 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_11");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement11() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement11(BlockState blockstate) {
        super(blockstate);
    }
}