package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockTintedGlass extends BlockGlass {
    public static final BlockProperties PROPERTIES = new BlockProperties(TINTED_GLASS);
    public static final BlockDefinition DEFINITION = BlockGlass.DEFINITION.toBuilder()
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTintedGlass() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTintedGlass(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Tinted Glass";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { toItem() };
    }

    }