package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteStainedGlass extends BlockGlassStained {
    public static final BlockProperties $1 = new BlockProperties(WHITE_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWhiteStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.WHITE;
    }
}