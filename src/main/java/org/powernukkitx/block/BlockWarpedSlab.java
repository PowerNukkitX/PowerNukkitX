package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWarpedSlab extends BlockSlab {
    public static final BlockProperties PROPERTIES = new BlockProperties(WARPED_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .resistance(3)
            .toolType(ItemTool.TYPE_AXE)
            .toolTier(0)
            .burnChance(-1)
            .burnAbility(0)
            .canHarvestWithHand(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWarpedSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWarpedSlab(BlockState blockstate) {
        super(blockstate, WARPED_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Warped";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return getId().equals(slab.getId());
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this);
    }

    
    }