package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockGrayWoolDoubleSlab extends BlockWoolDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_wool_double_slab", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayWoolDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayWoolDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Gray Wool";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:gray_wool_slab").getBlockState();
    }

}

