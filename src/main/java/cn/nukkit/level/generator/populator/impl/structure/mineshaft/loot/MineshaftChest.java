package cn.nukkit.level.generator.populator.impl.structure.mineshaft.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

//\\ ./data/behavior_packs/vanilla/loot_tables/chests/abandoned_mineshaft.json

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class MineshaftChest extends RandomizableContainer {

    private static final MineshaftChest INSTANCE = new MineshaftChest();

    public static MineshaftChest get() {
        return INSTANCE;
    }

    private MineshaftChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.GOLDEN_APPLE, 20))
                .register(new ItemEntry(Item.GOLDEN_APPLE_ENCHANTED, 1))
                .register(new ItemEntry(Item.NAME_TAG, 30))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 10)) //TODO: enchant_randomly
                .register(new ItemEntry(Item.IRON_PICKAXE, 5))
                .register(new ItemEntry(Item.AIR, 5));
        this.pools.put(pool1.build(), new RollEntry(1, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 10))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 5))
                .register(new ItemEntry(Item.REDSTONE, 0, 9, 4, 5))
                .register(new ItemEntry(Item.DYE, 4, 9, 4, 5))
                .register(new ItemEntry(Item.DIAMOND, 0, 2, 3))
                .register(new ItemEntry(Item.COAL, 0, 8, 3, 10))
                .register(new ItemEntry(Item.BREAD, 0, 3, 15))
                .register(new ItemEntry(Item.MELON_SEEDS, 0, 4, 2, 10))
                .register(new ItemEntry(Item.PUMPKIN_SEEDS, 0, 4, 2, 10))
                .register(new ItemEntry(Item.BEETROOT_SEEDS, 0, 4, 2, 10));
        this.pools.put(pool2.build(), new RollEntry(4, 2, pool2.getTotalWeight()));

        PoolBuilder pool3 = new PoolBuilder()
                .register(new ItemEntry(Item.RAIL, 0, 8, 4, 20))
                .register(new ItemEntry(Item.POWERED_RAIL, 0, 4, 5))
                .register(new ItemEntry(Item.DETECTOR_RAIL, 0, 4, 5))
                .register(new ItemEntry(Item.ACTIVATOR_RAIL, 0, 4, 5))
                .register(new ItemEntry(Item.TORCH, 0, 16, 15));
        this.pools.put(pool3.build(), new RollEntry(3, pool3.getTotalWeight()));
    }
}
