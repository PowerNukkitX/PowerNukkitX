package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockCyanConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:cyan_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCyanConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCyanConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Cyan Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:cyan_concrete_slab").getBlockState();
    }

}

