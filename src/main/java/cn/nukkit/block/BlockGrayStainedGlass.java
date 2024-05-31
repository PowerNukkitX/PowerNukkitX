package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

public class BlockGrayStainedGlass extends BlockGlassStained {
    public static final BlockProperties $1 = new BlockProperties(GRAY_STAINED_GLASS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayStainedGlass() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGrayStainedGlass(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    @NotNull public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }
}