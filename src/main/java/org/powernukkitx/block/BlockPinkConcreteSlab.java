package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:pink_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Pink Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

