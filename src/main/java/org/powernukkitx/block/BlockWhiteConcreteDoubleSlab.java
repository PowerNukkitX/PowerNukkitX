package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockWhiteConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:white_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWhiteConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWhiteConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "White Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:white_concrete_slab").getBlockState();
    }

}

