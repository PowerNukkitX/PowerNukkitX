package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaStainedGlassPane extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_stained_glass_pane");

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaStainedGlassPane() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaStainedGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}