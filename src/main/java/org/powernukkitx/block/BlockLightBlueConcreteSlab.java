package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:light_blue_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "LightBlue Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

