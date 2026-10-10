package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


/**
 * @author good777LUCKY
 */
public class BlockNetherreactor extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHERREACTOR);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(10)
            .resistance(6)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canHarvestWithHand(false)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherreactor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherreactor(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Nether Reactor Core";
    }

    
    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    Item.get(ItemID.DIAMOND, 0, 3),
                    Item.get(ItemID.IRON_INGOT, 0, 6)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
