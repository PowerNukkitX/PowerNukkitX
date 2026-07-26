package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement17 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_17");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement17() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement17(BlockState blockstate) {
        super(blockstate);
    }
}