package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedSulfurDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_SULFUR_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedSulfurDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedSulfurDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Polished Sulfur";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockSulfurSlab.PROPERTIES.getDefaultState();
    }

    }