package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.jetbrains.annotations.NotNull;

import static org.powernukkitx.block.property.CommonBlockProperties.GROWING_PLANT_AGE;


public class BlockCaveVinesHeadWithBerries extends BlockCaveVines {
    public static final BlockProperties PROPERTIES = new BlockProperties(CAVE_VINES_HEAD_WITH_BERRIES, GROWING_PLANT_AGE);
    public static final BlockDefinition DEFINITION = BlockCaveVines.DEFINITION.toBuilder()
            .lightEmission(14)
            .isTransparent(true)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCaveVinesHeadWithBerries() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCaveVinesHeadWithBerries(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Cave Vines Head With Berries";
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{Item.get(ItemID.GLOW_BERRIES)};
    }
}
