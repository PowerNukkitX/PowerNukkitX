package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author GoodLucky777
 */
public class BlockTuff extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(TUFF);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(1.5)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    public BlockTuff() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockTuff(BlockState blockState) {
        super(blockState, DEFINITION);
    }
    
    @Override
    public String getName() {
        return "Tuff";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
