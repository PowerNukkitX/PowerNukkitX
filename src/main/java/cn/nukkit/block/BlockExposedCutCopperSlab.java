package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties $1 = new BlockProperties(EXPOSED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockExposedCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockExposedCutCopperSlab(BlockState blockstate) {
        super(blockstate, EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }

    
    /**
     * @deprecated 
     */
    protected BlockExposedCutCopperSlab(BlockState blockstate, String doubleSlabId) {
        super(blockstate, doubleSlabId);
    }

    @Override
    @NotNull
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}