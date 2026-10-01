package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.block.definition.BlockDefinition;
import org.jetbrains.annotations.NotNull;


public class BlockAndesiteDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(ANDESITE_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);

    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockAndesiteDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockAndesiteDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Andesite";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockAndesiteSlab.PROPERTIES.getDefaultState();
    }
}