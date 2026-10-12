package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedTuffSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_TUFF_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedTuffSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedTuffSlab(BlockState blockstate) {
        super(blockstate, POLISHED_TUFF_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Polished Tuff";
    }

    @Override
    public String getName() {
        return "Polished Tuff Slab";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    
    }