package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement87 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_87");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement87() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement87(BlockState blockstate) {
        super(blockstate);
    }
}