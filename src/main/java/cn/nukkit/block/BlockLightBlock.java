package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlock extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(LIGHT_BLOCK, CommonBlockProperties.BLOCK_LIGHT_LEVEL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlock() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLightBlock(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Light Block";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getLightLevel() {
        return getPropertyValue(CommonBlockProperties.BLOCK_LIGHT_LEVEL);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }
}