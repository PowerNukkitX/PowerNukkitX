package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockBrownWoolSlab extends BlockWoolSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:brown_wool_slab", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBrownWoolSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBrownWoolSlab(BlockState blockState) {
        super(blockState, "minecraft:brown_wool_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Brown Wool";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

