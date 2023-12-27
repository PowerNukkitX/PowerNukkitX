package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockRedStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:red_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}