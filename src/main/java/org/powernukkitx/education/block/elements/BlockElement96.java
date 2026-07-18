package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
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