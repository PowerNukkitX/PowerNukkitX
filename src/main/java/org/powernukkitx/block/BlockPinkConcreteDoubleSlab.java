package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPinkConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:pink_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPinkConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPinkConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Pink Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:pink_concrete_slab").getBlockState();
    }

}

