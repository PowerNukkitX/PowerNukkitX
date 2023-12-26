package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGreenStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}