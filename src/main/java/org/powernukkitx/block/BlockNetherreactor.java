package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;


/**
 * @author good777LUCKY
 */
public class BlockNetherreactor extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(NETHERREACTOR);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockNetherreactor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockNetherreactor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Nether Reactor Core";
    }
    
    @Override
    public double getHardness() {
        return 10;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
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
