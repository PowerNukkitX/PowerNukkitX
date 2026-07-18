package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement113 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_113");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement113() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement113(BlockState blockstate) {
        super(blockstate);
    }
}