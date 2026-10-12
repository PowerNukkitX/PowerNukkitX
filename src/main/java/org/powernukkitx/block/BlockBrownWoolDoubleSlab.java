package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockBrownWoolDoubleSlab extends BlockWoolDoubleSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_wool_double_slab", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownWoolDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownWoolDoubleSlab(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getSlabName() {
        return "Brown Wool";
    }

    @Override
    public BlockState getSingleSlab() {
        return Block.get("minecraft:brown_wool_slab").getBlockState();
    }

}

