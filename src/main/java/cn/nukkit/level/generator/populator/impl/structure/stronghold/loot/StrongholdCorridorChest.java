package cn.nukkit.level.generator.populator.impl.structure.stronghold.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

//\\ ./data/behavior_packs/vanilla/loot_tables/chests/stronghold_corridor.json
@PowerNukkitXOnly
@Since("1.19.21-r2")
public class StrongholdCorridorChest extends RandomizableContainer {

    private static final StrongholdCorridorChest INSTANCE = new StrongholdCorridorChest();

    private StrongholdCorridorChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.ENDER_PEARL, 50))
                .register(new ItemEntry(Item.EMERALD, 0, 3, 15))
                .register(new ItemEntry(Item.DIAMOND, 0, 3, 15))
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 50))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 25))
                .register(new ItemEntry(Item.REDSTONE, 0, 9, 4, 25))
                .register(new ItemEntry(Item.BREAD, 0, 3, 75))
                .register(new ItemEntry(Item.APPLE, 0, 3, 75))
                .register(new ItemEntry(Item.IRON_PICKAXE, 25))
                .register(new ItemEntry(Item.IRON_SWORD, 25))
                .register(new ItemEntry(Item.IRON_CHESTPLATE, 25))
                .register(new ItemEntry(Item.IRON_HELMET, 25))
                .register(new ItemEntry(Item.IRON_LEGGINGS, 25))
                .register(new ItemEntry(Item.IRON_BOOTS, 25))
                .register(new ItemEntry(Item.GOLDEN_APPLE, 5))
                .register(new ItemEntry(Item.SADDLE, 5))
                .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.GOLD_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 6)); //TODO: treasure enchant_with_levels 30
        this.pools.put(pool1.build(), new RollEntry(3, 2, pool1.getTotalWeight()));
    }

    public static StrongholdCorridorChest get() {
        return INSTANCE;
    }
}
