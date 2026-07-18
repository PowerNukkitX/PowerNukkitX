package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement74 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_74");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement74() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement74(BlockState blockstate) {
        super(blockstate);
    }
}