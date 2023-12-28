package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMud extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:mud");

    public BlockMud() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMud(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Mud";
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
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

    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

}
