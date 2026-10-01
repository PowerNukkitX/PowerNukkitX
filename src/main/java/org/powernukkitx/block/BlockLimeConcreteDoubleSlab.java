package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLimeConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:lime_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLimeConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLimeConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Lime Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:lime_concrete_slab").getBlockState();
    }

}

