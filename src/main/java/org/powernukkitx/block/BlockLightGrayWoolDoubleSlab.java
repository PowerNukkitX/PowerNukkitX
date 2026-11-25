package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockLightGrayWoolDoubleSlab extends BlockWoolDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(LIGHT_GRAY_WOOL_DOUBLE_SLAB, Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockLightGrayWoolDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockLightGrayWoolDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "LightGray Wool";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:light_gray_wool_slab").getBlockState();
    }

}

