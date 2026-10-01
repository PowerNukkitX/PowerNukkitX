package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPoplarTrapdoor extends BlockTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(POPLAR_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPoplarTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPoplarTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Poplar Trapdoor";
    }
}
