package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCarrots extends BlockCrops {
    public static final BlockProperties PROPERTIES = new BlockProperties(CARROTS, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCarrots() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCarrots(BlockState blockstate) {
        super(blockstate);
    }

    @Override
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

        int drops = 2;
        int attempts = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < attempts; i++) {
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