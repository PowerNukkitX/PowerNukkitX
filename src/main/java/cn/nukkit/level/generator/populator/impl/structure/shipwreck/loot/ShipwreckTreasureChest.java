package cn.nukkit.level.generator.populator.impl.structure.shipwreck.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class ShipwreckTreasureChest extends RandomizableContainer {

    private static final ShipwreckTreasureChest INSTANCE = new ShipwreckTreasureChest();

    public static ShipwreckTreasureChest get() {
        return INSTANCE;
    }

    private ShipwreckTreasureChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 90))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 5, 10))
                .register(new ItemEntry(Item.EMERALD, 0, 5, 40))
                .register(new ItemEntry(Item.DIAMOND, 5))
                .register(new ItemEntry(Item.EXPERIENCE_BOTTLE, 5));
        this.pools.put(pool1.build(), new RollEntry(6, 3, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.IRON_INGOT, 0, 10, 50))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 10, 10))
                .register(new ItemEntry(Item.DYE, 4, 10, 20));
        this.pools.put(pool2.build(), new RollEntry(5, 2, pool2.getTotalWeight()));
    }
}
