package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement103 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_103");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement103() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement103(BlockState blockstate) {
        super(blockstate);
    }
}