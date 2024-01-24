package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCobbledDeepslateWall extends BlockWallBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLED_DEEPSLATE_WALL, CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobbledDeepslateWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobbledDeepslateWall(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Cobbled Deepslate Wall";
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public double getHardness() {
        return 3.5;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }
}