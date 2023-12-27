package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHardGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:hard_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHardGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockHardGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}