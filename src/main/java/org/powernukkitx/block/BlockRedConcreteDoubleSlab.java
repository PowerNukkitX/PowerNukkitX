package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockRedConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(RED_CONCRETE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockRedConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRedConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Red Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:red_concrete_slab").getBlockState();
    }

}

