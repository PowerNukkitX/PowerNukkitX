package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:orange_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:orange_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Orange Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

