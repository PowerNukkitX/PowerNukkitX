package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBrownConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Brown Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:brown_concrete_slab").getBlockState();
    }

}

