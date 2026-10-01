package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


public class BlockStoneBrickDoubleSlab extends BlockDoubleSlabBase {
    public static final BlockProperties PROPERTIES = new BlockProperties(STONE_BRICK_DOUBLE_SLAB, CommonBlockProperties.MINECRAFT_VERTICAL_HALF);
    public static final BlockDefinition DEFINITION = BlockDoubleSlabBase.DEFINITION.toBuilder()
            .toolType(ItemTool.TYPE_PICKAXE)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStoneBrickDoubleSlab() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStoneBrickDoubleSlab(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getSlabName() {
        return "Stone Brick";
    }

    @Override
    public BlockState getSingleSlab() {
        return BlockStoneBrickSlab.PROPERTIES.getDefaultState();
    }

    }