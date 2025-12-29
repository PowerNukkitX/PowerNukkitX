package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement49 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_49");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement49() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement49(BlockState blockstate) {
        super(blockstate);
    }
}