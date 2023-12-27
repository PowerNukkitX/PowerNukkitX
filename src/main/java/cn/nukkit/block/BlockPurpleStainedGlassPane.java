package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockPurpleStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}