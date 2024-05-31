package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWeatheredCutCopperSlab extends BlockCutCopperSlab {
    public static final BlockProperties $1 = new BlockProperties(WEATHERED_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockWeatheredCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockWeatheredCutCopperSlab(BlockState blockstate) {
        super(blockstate, WEATHERED_DOUBLE_CUT_COPPER_SLAB);
    }

    
    /**
     * @deprecated 
     */
    protected BlockWeatheredCutCopperSlab(BlockState blockstate, String doubleSlab) {
        super(blockstate, doubleSlab);
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}