package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockCoalBlock extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(COAL_BLOCK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(5)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .burnChance(5)
            .burnAbility(5)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoalBlock() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoalBlock(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Block of Coal";
    }

    
    }