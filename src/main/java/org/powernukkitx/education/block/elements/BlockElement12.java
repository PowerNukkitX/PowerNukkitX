package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement12 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_12");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement12() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement12(BlockState blockstate) {
        super(blockstate);
    }
}