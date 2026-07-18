package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement78 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_78");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement78() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement78(BlockState blockstate) {
        super(blockstate);
    }
}