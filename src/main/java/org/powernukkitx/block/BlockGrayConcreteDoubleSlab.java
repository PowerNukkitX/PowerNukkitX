package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockGrayConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Gray Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:gray_concrete_slab").getBlockState();
    }

}

