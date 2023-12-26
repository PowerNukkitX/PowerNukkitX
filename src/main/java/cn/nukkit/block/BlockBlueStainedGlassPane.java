package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockBlueStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:blue_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}