package cn.nukkit.block;

import org.jetbrains.annotations.NotNull;

public class BlockLightBlueCarpet extends BlockCarpet {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_BLUE_CARPET);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueCarpet() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlueCarpet(BlockState blockstate) {
        super(blockstate);
    }
}