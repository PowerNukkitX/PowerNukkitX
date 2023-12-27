package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockGrayStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}