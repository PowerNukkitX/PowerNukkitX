package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockBambooDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(BAMBOO_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .hardness(2)
            .resistance(15)
            .toolType(ItemTool.TYPE_AXE)
            .burnChance(5)
            .burnAbility(20)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBambooDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBambooDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Bamboo";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockBambooSlab.PROPERTIES.getDefaultState();
    }
}