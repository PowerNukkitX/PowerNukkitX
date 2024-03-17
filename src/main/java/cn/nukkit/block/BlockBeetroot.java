package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBeetrootSeeds;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 * @since 2015/11/22
 */
public class BlockBeetroot extends BlockCrops {
    public static final BlockProperties PROPERTIES = new BlockProperties(BEETROOT, CommonBlockProperties.GROWTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBeetroot() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBeetroot(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Beetroot Block";
    }

    @Override
    public Item toItem() {
        return new ItemBeetrootSeeds();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!isFullyGrown()) {
            return new Item[]{Item.get(ItemID.BEETROOT_SEEDS)};
        }
        
        int seeds = 1;
        int attempts = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < attempts; i++) {
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                seeds++;
            }
        }

        return new Item[]{
                Item.get(BlockID.BEETROOT),
                Item.get(ItemID.BEETROOT_SEEDS, 0, seeds)
        };
    }
}
