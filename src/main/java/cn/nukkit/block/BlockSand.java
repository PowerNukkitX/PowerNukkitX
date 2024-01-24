package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.SandType;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */

public class BlockSand extends BlockFallable {
    public static final BlockProperties PROPERTIES = new BlockProperties(SAND, CommonBlockProperties.SAND_TYPE);
    @Override
    @NotNull public BlockProperties getProperties() {
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
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return getPropertyValue(CommonBlockProperties.SAND_TYPE) == SandType.NORMAL ? "Sand" : "Red Sand";
    }

}
