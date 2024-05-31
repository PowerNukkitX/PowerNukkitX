package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockGildedBlackstone extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(GILDED_BLACKSTONE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockGildedBlackstone() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockGildedBlackstone(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Gilded Blackstone";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }

        int dropOdds;
        int $2 = 0;
        Enchantment $3 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }

        dropOdds = switch (fortune) {
            case 0 -> 10;
            case 1 -> 7;
            case 2 -> 4;
            default -> 1;
        };

        ThreadLocalRandom $4 = ThreadLocalRandom.current();
        if (dropOdds > 1 && random.nextInt(dropOdds) != 0) {
            return new Item[] { toItem() };
        }

        return new Item[] { Item.get(ItemID.GOLD_NUGGET, 0, random.nextInt(2, 6)) };
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 1.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 6;
    }
}