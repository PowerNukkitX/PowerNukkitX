package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}