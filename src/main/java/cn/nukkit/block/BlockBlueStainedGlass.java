package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockBlueStainedGlass extends BlockGlassStained {
    public static final BlockProperties $1 = new BlockProperties(BLUE_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBlueStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }
}