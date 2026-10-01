package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:purple_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Purple Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

