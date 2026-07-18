package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockCobblestone extends BlockSolid {

    public static final BlockProperties PROPERTIES = new BlockProperties(COBBLESTONE);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCobblestone() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCobblestone(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cobblestone";
    }

    
    }
