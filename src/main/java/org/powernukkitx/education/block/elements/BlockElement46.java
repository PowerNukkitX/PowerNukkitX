package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement46 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_46");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement46() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement46(BlockState blockstate) {
        super(blockstate);
    }
}