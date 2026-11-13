package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockBlueConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(BLUE_CONCRETE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBlueConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBlueConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Blue Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:blue_concrete_slab").getBlockState();
    }

}

