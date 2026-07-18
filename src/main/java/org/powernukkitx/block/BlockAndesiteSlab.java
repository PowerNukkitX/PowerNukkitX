package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.block.definition.BlockDefinition;
import org.jetbrains.annotations.NotNull;

public class BlockAndesiteSlab extends BlockSlab {

    public static final BlockProperties PROPERTIES = new BlockProperties(ANDESITE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public static final BlockDefinition DEFINITION = BlockSlab.DEFINITION.toBuilder()
            .hardness(1.5)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    public BlockAndesiteSlab(BlockState blockState) {
        super(blockState, ANDESITE_DOUBLE_SLAB, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Andesite";
    }

    @Override
    public boolean isSameType(BlockSlab slab) {
        return slab.getId().equals(this.getId());
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }
}
