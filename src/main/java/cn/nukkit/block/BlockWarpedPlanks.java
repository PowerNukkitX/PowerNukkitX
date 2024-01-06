package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedPlanks extends BlockPlanks {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_PLANKS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedPlanks() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedPlanks(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Warped Planks";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

}