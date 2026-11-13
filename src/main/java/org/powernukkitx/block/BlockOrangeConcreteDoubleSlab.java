package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOrangeConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(ORANGE_CONCRETE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOrangeConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOrangeConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Orange Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:orange_concrete_slab").getBlockState();
    }

}

