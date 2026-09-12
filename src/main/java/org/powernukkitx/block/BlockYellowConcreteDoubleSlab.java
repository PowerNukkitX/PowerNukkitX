package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockYellowConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:yellow_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockYellowConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockYellowConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Yellow Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:yellow_concrete_slab").getBlockState();
    }

}

