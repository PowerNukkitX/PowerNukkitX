package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:white_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "White Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

