package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDiamond;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockDiamondOre extends BlockOre {
    public static final BlockProperties $1 = new BlockProperties(DIAMOND_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockDiamondOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDiamondOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    protected @Nullable 
    /**
     * @deprecated 
     */
    String getRawMaterial() {
        return ItemID.DIAMOND;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Diamond Ore";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            int $2 = 1;
            Enchantment $3 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                $4nt $1 = ThreadLocalRandom.current().nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count = i + 1;
            }

            return new Item[]{
                    new ItemDiamond(0, count)
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(3, 8);
    }
}