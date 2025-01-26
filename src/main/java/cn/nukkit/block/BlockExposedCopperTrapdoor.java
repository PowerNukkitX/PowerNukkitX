package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockExposedCopperTrapdoor extends BlockCopperTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPOSED_COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockExposedCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockExposedCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Exposed Copper Trapdoor";
    }
}