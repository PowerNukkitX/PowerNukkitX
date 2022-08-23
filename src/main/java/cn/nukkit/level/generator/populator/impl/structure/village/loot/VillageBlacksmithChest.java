package cn.nukkit.level.generator.populator.impl.structure.village.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

//\\ ./data/behavior_packs/vanilla/loot_tables/chests/village_blacksmith.json (1.9.0.15)
@PowerNukkitXOnly
@Since("1.19.21-r6")
public class VillageBlacksmithChest extends RandomizableContainer {

    private static final VillageBlacksmithChest INSTANCE = new VillageBlacksmithChest();

    public static VillageBlacksmithChest get() {
        return INSTANCE;
    }

    private VillageBlacksmithChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.DIAMOND, 0, 3, 3))
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 10))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 5))
                .register(new ItemEntry(Item.BREAD, 0, 3, 15))
                .register(new ItemEntry(Item.APPLE, 0, 3, 15))
                .register(new ItemEntry(Item.IRON_PICKAXE, 5))
                .register(new ItemEntry(Item.IRON_SWORD, 5))
                .register(new ItemEntry(Item.IRON_CHESTPLATE, 5))
                .register(new ItemEntry(Item.IRON_HELMET, 5))
                .register(new ItemEntry(Item.IRON_LEGGINGS, 5))
                .register(new ItemEntry(Item.IRON_BOOTS, 5))
                .register(new ItemEntry(Item.OBSIDIAN, 0, 7, 3, 5))
                .register(new ItemEntry(Item.SAPLING, 0, 7, 3, 5))
                .register(new ItemEntry(Item.SADDLE, 3))
                .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 1))
                .register(new ItemEntry(Item.GOLD_HORSE_ARMOR, 1))
                .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 1));
        this.pools.put(pool1.build(), new RollEntry(8, 3, pool1.getTotalWeight()));
    }
}
