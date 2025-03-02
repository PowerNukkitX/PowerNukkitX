package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
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
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.25;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.25;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.75;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.50;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.75;
    }

    @Override
    public boolean canPassThrough() {
        return false;
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