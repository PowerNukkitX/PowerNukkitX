package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcreteSlab extends BlockConcreteSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_concrete_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaConcreteSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaConcreteSlab(BlockState blockState) {
        super(blockState, "minecraft:magenta_concrete_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Magenta Concrete";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

