package cn.nukkit.education.block.elements;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement90 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_90");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement90() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement90(BlockState blockstate) {
        super(blockstate);
    }
}