package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class BlockPotatoes  extends BlockCrops {
    public static final BlockProperties PROPERTIES = new BlockProperties(POTATOES, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPotatoes() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPotatoes(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.POTATO);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!isFullyGrown()) {
            return new Item[]{
                    Item.get(ItemID.POTATO)
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

        if (random.nextInt(5) < 1) { // 1/5, 0.2
            return new Item[]{
                    Item.get(ItemID.POTATO,0, drops),
                    Item.get(ItemID.POISONOUS_POTATO)
            };
        } else {
            return new Item[]{
                    Item.get(ItemID.POTATO, 0, drops)
            };
        }
    }
}