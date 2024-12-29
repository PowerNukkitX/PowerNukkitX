package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockSand extends BlockFallable implements Natural {
    public static final BlockProperties PROPERTIES = new BlockProperties(SAND);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSand() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSand(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        if (this instanceof BlockRedSand) {
            return "Red Sand";
        } else {
            return "Sand";
        }
    }

}
