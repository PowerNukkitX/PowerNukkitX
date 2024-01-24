package cn.nukkit.block;


import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

public class BlockPolishedBasalt extends BlockBasalt {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BASALT, PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBasalt() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBasalt(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Basalt";
    }
}
