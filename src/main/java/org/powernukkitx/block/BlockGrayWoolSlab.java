package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockGrayWoolSlab extends BlockWoolSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:gray_wool_slab", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockGrayWoolSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockGrayWoolSlab(BlockState blockState) {
        super(blockState, "minecraft:gray_wool_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Gray Wool";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

