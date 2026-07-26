package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement70 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_70");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement70() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement70(BlockState blockstate) {
        super(blockstate);
    }
}