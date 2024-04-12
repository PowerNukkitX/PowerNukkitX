package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlock extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_BLOCK, CommonBlockProperties.BLOCK_LIGHT_LEVEL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Light Block";
    }

    @Override
    public int getLightLevel() {
        return getPropertyValue(CommonBlockProperties.BLOCK_LIGHT_LEVEL);
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