package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockHeavyCore extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(HEAVY_CORE);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHeavyCore() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHeavyCore(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Heavy Core";
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public double getHardness() {
        return 10;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean breaksWhenMoved() {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}