package cn.nukkit.level.generator.populator.impl.structure.desertpyramid.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class DesertPyramidChest extends RandomizableContainer {

    private static final DesertPyramidChest INSTANCE = new DesertPyramidChest();

    private DesertPyramidChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.DIAMOND, 0, 3, 1, 5))
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 1, 15))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 7, 2, 15))
                .register(new ItemEntry(Item.EMERALD, 0, 3, 1, 15))
                .register(new ItemEntry(Item.BONE, 0, 6, 4, 25))
                .register(new ItemEntry(Item.SPIDER_EYE, 0, 3, 1, 25))
                .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 7, 3, 25))
                .register(new ItemEntry(Item.SADDLE, 20))
                .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 15))
                .register(new ItemEntry(Item.GOLD_HORSE_ARMOR, 10))
                .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 20)) //TODO: ench nbt
                .register(new ItemEntry(Item.GOLDEN_APPLE, 20))
                .register(new ItemEntry(Item.GOLDEN_APPLE_ENCHANTED, 2))
                .register(new ItemEntry(Item.AIR, 15));
        this.pools.put(pool1.build(), new RollEntry(4, 2, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.BONE, 0, 8, 1, 10))
                .register(new ItemEntry(Item.GUNPOWDER, 0, 8, 1, 10))
                .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 8, 1, 10))
                .register(new ItemEntry(Item.STRING, 0, 8, 1, 10))
                .register(new ItemEntry(Item.SAND, 0, 8, 1, 10));
        this.pools.put(pool2.build(), new RollEntry(4, pool2.getTotalWeight()));
    }

    public static DesertPyramidChest get() {
        return INSTANCE;
    }
}
