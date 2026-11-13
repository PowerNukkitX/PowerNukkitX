package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIME_CONCRETE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:lime_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Lime Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

