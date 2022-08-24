package cn.nukkit.level.generator.populator.impl.structure.dungeon.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

// ./data/behavior_packs/vanilla/loot_tables/chests/simple_dungeon.json (1.16.20.3)
@PowerNukkitXOnly
@Since("1.19.21-r2")
public class DungeonChest extends RandomizableContainer {

    private static final DungeonChest INSTANCE = new DungeonChest();

    public static DungeonChest get() {
        return INSTANCE;
    }

    private DungeonChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.SADDLE, 20))
                .register(new ItemEntry(Item.GOLDEN_APPLE, 15))
                .register(new ItemEntry(Item.GOLDEN_APPLE_ENCHANTED, 2))
                .register(new ItemEntry(Item.RECORD_13, 15))
                .register(new ItemEntry(Item.RECORD_CAT, 15))
                .register(new ItemEntry(Item.NAME_TAG, 20))
                .register(new ItemEntry(Item.GOLD_HORSE_ARMOR, 10))
                .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 15))
                .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 10)); //TODO: ench nbt
        this.pools.put(pool1.build(), new RollEntry(3, 1, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 4, 10))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 4, 5))
                .register(new ItemEntry(Item.BREAD, 20))
                .register(new ItemEntry(Item.WHEAT, 0, 4, 20))
                .register(new ItemEntry(Item.BUCKET, 10))
                .register(new ItemEntry(Item.REDSTONE, 0, 4, 15))
                .register(new ItemEntry(Item.COAL, 0, 4, 15))
                .register(new ItemEntry(Item.MELON_SEEDS, 0, 4, 2, 10))
                .register(new ItemEntry(Item.PUMPKIN_SEEDS, 0, 4, 2, 10))
                .register(new ItemEntry(Item.BEETROOT_SEEDS, 0, 4, 2, 10));
        this.pools.put(pool2.build(), new RollEntry(4, 1, pool2.getTotalWeight()));

        PoolBuilder pool3 = new PoolBuilder()
                .register(new ItemEntry(Item.BONE, 0, 8, 10))
                .register(new ItemEntry(Item.GUNPOWDER, 0, 8, 10))
                .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 8, 10))
                .register(new ItemEntry(Item.STRING, 0, 8, 10));
        this.pools.put(pool3.build(), new RollEntry(3, pool3.getTotalWeight()));
    }
}
