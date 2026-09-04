package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.jetbrains.annotations.NotNull;

public class BlockNetherSprouts extends BlockHanging {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHER_SPROUTS);
    public static final BlockDefinition DEFINITION = BlockHanging.DEFINITION.toBuilder()
            .burnChance(5)
            .canBeReplaced(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherSprouts() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherSprouts(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Nether Sprouts Block";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{ toItem() };
        }
        return Item.EMPTY_ARRAY;
    }

    
    @Override
    public int getSnowloggingLevel() {
        return 1;
    }

    }