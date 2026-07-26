package org.powernukkitx.education.block.elements;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockElement27 extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:element_27");

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockElement27() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockElement27(BlockState blockstate) {
        super(blockstate);
    }
}