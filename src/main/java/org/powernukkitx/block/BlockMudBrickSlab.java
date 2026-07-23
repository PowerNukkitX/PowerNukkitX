package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;


import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(MUD_BRICK_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(1.5)
            .resistance(3)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockMudBrickSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockMudBrickSlab(BlockState blockState) {
        super(blockState, MUD_BRICK_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Mud Brick";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(getId());
    }

    }
