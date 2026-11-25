package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGreenConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(GREEN_CONCRETE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Green Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:green_concrete_slab").getBlockState();
    }

}

