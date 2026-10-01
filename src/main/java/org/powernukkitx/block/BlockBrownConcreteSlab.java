package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:brown_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Brown Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

