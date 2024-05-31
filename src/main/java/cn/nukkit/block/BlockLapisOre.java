package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockLapisOre extends BlockOre {
    public static final BlockProperties $1 = new BlockProperties(LAPIS_ORE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockLapisOre() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockLapisOre(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Lapis Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= getToolTier()) {
            ThreadLocalRandom $2 = ThreadLocalRandom.current();
            int $3 = 4 + random.nextInt(5);
            Enchantment $4 = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                $5nt $1 = random.nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count *= (i + 1);
            }
            Item $6 = Item.get(ItemID.LAPIS_LAZULI);
            itemRaw.setCount(count);
            return new Item[]{itemRaw};
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolTier() {
        return ItemTool.TIER_STONE;
    }

    @Override
    protected @Nullable 
    /**
     * @deprecated 
     */
    String getRawMaterial() {
        return ItemID.LAPIS_LAZULI;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(2, 6);
    }
}