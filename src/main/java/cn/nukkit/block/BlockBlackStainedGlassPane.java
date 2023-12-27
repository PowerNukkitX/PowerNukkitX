package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockBlackStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:black_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlackStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlackStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}