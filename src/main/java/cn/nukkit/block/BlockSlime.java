package cn.nukkit.block;

import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 21.02.2016
 */
public class BlockSlime extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SLIME);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSlime() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSlime(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public String getName() {
        return "Slime Block";
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public int getLightFilter() {
        return 1;
    }

    @Override
    public boolean canSticksBlock() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }
}
