package cn.nukkit.block;

import cn.nukkit.blockproperty.value.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author joserobjr
 * @since 2021-06-14
 */

public class BlockDoubleSlabCopperCut extends BlockDoubleSlabCopperBase {

    public BlockDoubleSlabCopperCut() {
        this(0);
    }


    public BlockDoubleSlabCopperCut(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_CUT_COPPER_SLAB;
    }


    @Override
    public String getSlabName() {
        StringBuilder sb = new StringBuilder(30);
        if (isWaxed()) {
            sb.append("Waxed ");
        }
        OxidizationLevel oxidizationLevel = getOxidizationLevel();
        if (!OxidizationLevel.UNAFFECTED.equals(oxidizationLevel)) {
            String name = oxidizationLevel.name();
            sb.append(name.charAt(0)).append(name.substring(1).toLowerCase(Locale.ENGLISH)).append(' ');
        }
        return sb.append("Cut Copper").toString();
    }


    @Override
    public int getSingleSlabId() {
        return CUT_COPPER_SLAB;
    }


    @Override
    protected int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed? WAXED_DOUBLE_CUT_COPPER_SLAB : DOUBLE_CUT_COPPER_SLAB;
            case EXPOSED:
                return waxed? WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB : EXPOSED_DOUBLE_CUT_COPPER_SLAB;
            case WEATHERED:
                return waxed? WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB : WEATHERED_DOUBLE_CUT_COPPER_SLAB;
            case OXIDIZED:
                return waxed? WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB : OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
            default:
                return getId();
        }
    }


    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
