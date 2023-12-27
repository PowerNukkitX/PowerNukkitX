package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockCyanStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}