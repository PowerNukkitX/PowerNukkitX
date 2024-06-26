package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedCopperBulb extends BlockCopperBulbBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_COPPER_BULB, CommonBlockProperties.LIT, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedCopperBulb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedCopperBulb(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    public boolean isWaxed() {
        return true;
    }

    @Override
    public int getLightLevel() {
        return getLit() ? 15 : 0;
    }
}