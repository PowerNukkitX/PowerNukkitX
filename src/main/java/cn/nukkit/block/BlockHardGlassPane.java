package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockHardGlassPane extends Block {
    public static final BlockProperties $1 = new BlockProperties(HARD_GLASS_PANE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockHardGlassPane() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockHardGlassPane(BlockState blockstate) {
        super(blockstate);
    }
}