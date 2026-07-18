package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement32 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_32");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement32() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement32(BlockState blockstate) {
        super(blockstate);
    }
}