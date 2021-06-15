package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.value.OxidizationLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * @author joserobjr
 * @since 2021-06-15
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockStairsCopperCut extends BlockStairsCopperBase {
    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsCopperCut() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockStairsCopperCut(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        StringBuilder sb = new StringBuilder(30);
        if (isWaxed()) {
            sb.append("Waxed ");
        }
        OxidizationLevel oxidizationLevel = getOxidizationLevel();
        if (!OxidizationLevel.UNAFFECTED.equals(oxidizationLevel)) {
            String name = oxidizationLevel.name();
            sb.append(name.charAt(0)).append(name.substring(1).toLowerCase(Locale.ENGLISH)).append(' ');
        }
        return sb.append("Cut Copper Stairs").toString();
    }

    @Override
    public int getId() {
        return CUT_COPPER_STAIRS;
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
                return waxed? WAXED_CUT_COPPER_STAIRS : CUT_COPPER_STAIRS;
            case EXPOSED:
                return waxed? WAXED_EXPOSED_CUT_COPPER_STAIRS : EXPOSED_CUT_COPPER_STAIRS;
            case WEATHERED:
                return waxed? WAXED_WEATHERED_CUT_COPPER_STAIRS : WEATHERED_CUT_COPPER_STAIRS;
            case OXIDIZED:
                return waxed? WAXED_OXIDIZED_CUT_COPPER_STAIRS : OXIDIZED_CUT_COPPER_STAIRS;
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
