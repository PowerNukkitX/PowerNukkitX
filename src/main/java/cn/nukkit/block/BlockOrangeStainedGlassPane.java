package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockOrangeStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}