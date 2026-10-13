package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockLightGrayConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:light_gray_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "LightGray Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:light_gray_concrete_slab").getBlockState();
    }

}

