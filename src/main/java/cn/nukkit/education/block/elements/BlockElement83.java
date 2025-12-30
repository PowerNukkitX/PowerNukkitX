package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement83 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_83");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement83() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement83(BlockState blockstate) {
        super(blockstate);
    }
}