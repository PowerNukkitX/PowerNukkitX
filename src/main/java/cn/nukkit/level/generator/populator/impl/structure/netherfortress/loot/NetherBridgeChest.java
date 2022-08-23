package cn.nukkit.level.generator.populator.impl.structure.netherfortress.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.20-r6")
//\\ ./data/behavior_packs/vanilla/loot_tables/chests/nether_bridge.json
public class NetherBridgeChest extends RandomizableContainer {

    private static final NetherBridgeChest INSTANCE = new NetherBridgeChest();

    public static NetherBridgeChest get() {
        return INSTANCE;
    }

    private NetherBridgeChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        RandomizableContainer.PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.DIAMOND, 0, 3, 5))
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 5))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 3, 15))
                .register(new ItemEntry(Item.GOLDEN_SWORD, 5))
                .register(new ItemEntry(Item.GOLD_CHESTPLATE, 5))
                .register(new ItemEntry(Item.FLINT_AND_STEEL, 5))
                .register(new ItemEntry(Item.NETHER_WART, 0, 7, 3, 5))
                .register(new ItemEntry(Item.SADDLE, 10))
                .register(new ItemEntry(Item.GOLD_HORSE_ARMOR, 8))
                .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 3))
                .register(new ItemEntry(Item.OBSIDIAN, 0, 4, 2, 2));
        this.pools.put(pool1.build(), new RollEntry(4, 2, pool1.getTotalWeight()));
    }
}
