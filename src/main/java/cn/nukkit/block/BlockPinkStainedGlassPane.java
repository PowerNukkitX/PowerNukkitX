package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockPinkStainedGlassPane extends BlockGlassPaneStained {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PINK;
    }
}