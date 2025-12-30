package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement8 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_8");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement8() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement8(BlockState blockstate) {
        super(blockstate);
    }
}