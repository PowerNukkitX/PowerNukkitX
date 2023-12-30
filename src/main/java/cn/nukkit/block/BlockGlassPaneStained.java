package cn.nukkit.block;

import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 7.8.2017
 */
public abstract class BlockGlassPaneStained extends BlockGlassPane {

    public BlockGlassPaneStained(BlockState blockState) {
        super(blockState);
    }

    public abstract DyeColor getDyeColor();
}
