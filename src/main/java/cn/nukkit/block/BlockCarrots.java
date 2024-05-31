package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCarrots extends BlockCrops {
    public static final BlockProperties $1 = new BlockProperties(CARROTS, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockCarrots() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockCarrots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Carrot Block";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!isFullyGrown()) {
            return new Item[]{
                    Item.get(ItemID.CARROT)
            };
        }

        int $2 = 2;
        int $3 = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING));
        ThreadLocalRandom $4 = ThreadLocalRandom.current();
        for ($5nt $1 = 0; i < attempts; i++) {
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                drops++;
            }
        }

        return new Item[]{
                Item.get(ItemID.CARROT, 0, drops)
        };
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.CARROT);
    }
}