package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockSandstoneWall extends BlockWallBase {

    public static final BlockProperties PROPERTIES = new BlockProperties(
            SANDSTONE_WALL,
            CommonBlockProperties.WALL_CONNECTION_TYPE_EAST,
            CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH,
            CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH,
            CommonBlockProperties.WALL_CONNECTION_TYPE_WEST,
            CommonBlockProperties.WALL_POST_BIT
    );

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSandstoneWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSandstoneWall(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Sandstone Wall";
    }

    @Override
    public double getResistance() {
        return 0.8;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this.getProperties().getDefaultState().toBlock());
    }
}
