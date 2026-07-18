package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author xtypr
 * @since 2015/12/7
 */
public class BlockNetherBrick extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_BRICK);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(2)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    public BlockNetherBrick() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockNetherBrick(BlockState blockState) {
        super(blockState, DEFINITION);
    }

    public BlockNetherBrick(BlockState blockState, BlockDefinition definition) {
        super(blockState, definition);
    }

    @Override
    public String getName() {
        return "Nether Brick";
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
