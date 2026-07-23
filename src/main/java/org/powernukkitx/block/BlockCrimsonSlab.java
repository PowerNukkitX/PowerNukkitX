package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCrimsonSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(CRIMSON_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(3.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .burnChance(-1)
            .burnAbility(0)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCrimsonSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCrimsonSlab(BlockState blockstate) {
        super(blockstate, CRIMSON_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Crimson";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    }