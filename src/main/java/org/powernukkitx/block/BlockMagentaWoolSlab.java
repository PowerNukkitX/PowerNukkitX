package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.tags.BlockTags;

import java.util.Set;

public class BlockMagentaWoolSlab extends BlockWoolSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:magenta_wool_slab", Set.of(BlockTags.PNX_WOOL), CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMagentaWoolSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMagentaWoolSlab(BlockState blockState) {
        super(blockState, "minecraft:magenta_wool_double_slab");
    }

    @Override
    public String getSlabName() {
        return "Magenta Wool";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

}

