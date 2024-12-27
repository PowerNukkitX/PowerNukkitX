package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWaxedExposedCopperTrapdoor extends BlockCopperTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(WAXED_EXPOSED_COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWaxedExposedCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWaxedExposedCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Waxed Exposed Copper Trapdoor";
    }
}