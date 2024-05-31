package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockYellowStainedGlass extends BlockGlassStained {
    public static final BlockProperties $1 = new BlockProperties(YELLOW_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockYellowStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.YELLOW;
    }
}