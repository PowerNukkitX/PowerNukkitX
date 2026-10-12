package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:cyan_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Cyan Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

