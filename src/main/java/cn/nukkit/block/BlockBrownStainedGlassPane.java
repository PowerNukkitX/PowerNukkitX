package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBrownStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}