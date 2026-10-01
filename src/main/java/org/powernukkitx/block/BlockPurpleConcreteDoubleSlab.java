package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPurpleConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:purple_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPurpleConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPurpleConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Purple Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:purple_concrete_slab").getBlockState();
    }

}

