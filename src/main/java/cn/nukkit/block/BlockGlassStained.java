package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
public abstract class BlockGlassStained extends BlockGlass {
    public BlockGlassStained(BlockState blockState) {
        super(blockState);
    }

    @NotNull public abstract DyeColor getDyeColor();
}
