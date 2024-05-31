package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;

public class BlockDoubleCutCopperSlab extends BlockDoubleSlabCopperBase {
    public static final BlockProperties $1 = new BlockProperties(DOUBLE_CUT_COPPER_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleCutCopperSlab() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDoubleCutCopperSlab(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSlabName() {
        StringBuilder $2 = new StringBuilder(30);
        if (isWaxed()) {
            sb.append("Waxed ");
        }
        OxidizationLevel $3 = getOxidizationLevel();
        if (!OxidizationLevel.UNAFFECTED.equals(oxidizationLevel)) {
            String $4 = oxidizationLevel.name();
            sb.append(name.charAt(0)).append(name.substring(1).toLowerCase(Locale.ENGLISH)).append(' ');
        }
        return sb.append("Cut Copper").toString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getSingleSlabId() {
        return CUT_COPPER_SLAB;
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
            case UNAFFECTED -> waxed ? WAXED_DOUBLE_CUT_COPPER_SLAB : DOUBLE_CUT_COPPER_SLAB;
            case EXPOSED -> waxed ? WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB : EXPOSED_DOUBLE_CUT_COPPER_SLAB;
            case WEATHERED -> waxed ? WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB : WEATHERED_DOUBLE_CUT_COPPER_SLAB;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB : OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
        };
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}