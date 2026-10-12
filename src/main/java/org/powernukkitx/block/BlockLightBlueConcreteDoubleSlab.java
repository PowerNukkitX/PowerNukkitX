package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightBlueConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_blue_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightBlueConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightBlueConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "LightBlue Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:light_blue_concrete_slab").getBlockState();
    }

}

