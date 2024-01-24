package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Locale;

public class BlockCutCopperStairs extends BlockStairsCopperBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(CUT_COPPER_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCutCopperStairs(BlockState blockstate) {
        super(blockstate);
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
    protected String getCopperId(boolean waxed, @Nullable OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        return switch (oxidizationLevel) {
            case UNAFFECTED -> waxed ? WAXED_CUT_COPPER_STAIRS : CUT_COPPER_STAIRS;
            case EXPOSED -> waxed ? WAXED_EXPOSED_CUT_COPPER_STAIRS : EXPOSED_CUT_COPPER_STAIRS;
            case WEATHERED -> waxed ? WAXED_WEATHERED_CUT_COPPER_STAIRS : WEATHERED_CUT_COPPER_STAIRS;
            case OXIDIZED -> waxed ? WAXED_OXIDIZED_CUT_COPPER_STAIRS : OXIDIZED_CUT_COPPER_STAIRS;
        };
    }

    @Override
    @NotNull public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}