package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockGreenWoolDoubleSlab extends BlockWoolDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:green_wool_double_slab", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGreenWoolDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGreenWoolDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Green Wool";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:green_wool_slab").getBlockState();
    }

}

