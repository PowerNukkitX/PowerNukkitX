package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMagentaConcreteDoubleSlab extends BlockConcreteDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_concrete_double_slab", CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaConcreteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaConcreteDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Magenta Concrete";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:magenta_concrete_slab").getBlockState();
    }

}

