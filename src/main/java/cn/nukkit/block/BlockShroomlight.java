package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockShroomlight extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SHROOMLIGHT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockShroomlight() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockShroomlight(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Shroomlight";
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

}
