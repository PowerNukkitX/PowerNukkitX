package org.powernukkitx.education.block.glass;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockHardGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties(HARD_GLASS_PANE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}