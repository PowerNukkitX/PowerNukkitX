package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLimeStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}