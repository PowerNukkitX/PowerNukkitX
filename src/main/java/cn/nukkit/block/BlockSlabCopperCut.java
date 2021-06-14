package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author joserobjr
 * @since 2021-06-14
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockSlabCopperCut extends BlockSlabCopperBase {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCut() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockSlabCopperCut(int meta) {
        super(meta, DOUBLE_CUT_COPPER_SLAB);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    protected BlockSlabCopperCut(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return CUT_COPPER_SLAB;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
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

    @Since("FUTURE")
    @PowerNukkitOnly
    @Override
    protected int getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed? WAXED_CUT_COPPER_SLAB : CUT_COPPER_SLAB;
            case EXPOSED:
                return waxed? WAXED_EXPOSED_CUT_COPPER_SLAB : EXPOSED_CUT_COPPER_SLAB;
            case WEATHERED:
                return waxed? WAXED_WEATHERED_CUT_COPPER_SLAB : WEATHERED_CUT_COPPER_SLAB;
            case OXIDIZED:
                return waxed? WAXED_OXIDIZED_CUT_COPPER_SLAB : OXIDIZED_CUT_COPPER_SLAB;
            default:
                return getId();
        }
    }

    @Since("FUTURE")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
