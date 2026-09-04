package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockTuffBrickSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
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

    public BlockTuffBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTuffBrickSlab(BlockState blockstate) {
        super(blockstate, TUFF_BRICK_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Tuff Brick";
    }

    @Override
    public String getName() {
        return "Tuff Brick Slab";
    }

    
    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

}