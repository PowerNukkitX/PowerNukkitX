package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayStainedGlassPane extends BlockGlassPaneStained {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.LIGHT_GRAY;
    }
}