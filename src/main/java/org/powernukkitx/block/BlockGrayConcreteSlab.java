package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:gray_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Gray Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

