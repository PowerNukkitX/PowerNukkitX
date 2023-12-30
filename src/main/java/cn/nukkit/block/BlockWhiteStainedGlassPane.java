package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteStainedGlassPane extends BlockGlassPaneStained {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}