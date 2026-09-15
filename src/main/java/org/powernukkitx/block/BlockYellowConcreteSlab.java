package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:yellow_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Yellow Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

