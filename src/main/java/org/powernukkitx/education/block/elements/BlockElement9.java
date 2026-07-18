package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement9 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_9");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement9() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement9(BlockState blockstate) {
        super(blockstate);
    }
}