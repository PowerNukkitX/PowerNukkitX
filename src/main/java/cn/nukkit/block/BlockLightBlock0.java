package cn.nukkit.block;

import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlock0 extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK_0);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock0() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock0(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }
}