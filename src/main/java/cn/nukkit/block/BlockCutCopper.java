package cn.nukkit.block;

import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockCutCopper extends BlockCopperBase {
    public static final BlockProperties $1 = new BlockProperties(CUT_COPPER);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCutCopper() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCutCopper(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Cut Copper";
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_CUT_COPPER : CUT_COPPER;
            case EXPOSED -> waxed ? WAXED_EXPOSED_CUT_COPPER : EXPOSED_CUT_COPPER;
            case WEATHERED -> waxed ? WAXED_WEATHERED_CUT_COPPER : WEATHERED_CUT_COPPER;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_CUT_COPPER : OXIDIZED_CUT_COPPER;
        };
    }
}