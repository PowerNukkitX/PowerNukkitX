package cn.nukkit.level.generator.populator.impl.structure.pillageroutpost.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class PillagerOutpostChest extends RandomizableContainer {

    private static final PillagerOutpostChest INSTANCE = new PillagerOutpostChest();

    private PillagerOutpostChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.CROSSBOW, 1));
        this.pools.put(pool1.build(), new RollEntry(1, 0, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.WHEAT, 0, 5, 3, 7))
                .register(new ItemEntry(Item.POTATO, 0, 5, 2, 5))
                .register(new ItemEntry(Item.CARROT, 0, 5, 3, 5));
        this.pools.put(pool2.build(), new RollEntry(3, 2, pool2.getTotalWeight()));

        PoolBuilder pool3 = new PoolBuilder()
                .register(new ItemEntry(Item.LOG2, 1, 3, 2, 1));
        this.pools.put(pool3.build(), new RollEntry(3, 1, pool3.getTotalWeight()));

        PoolBuilder pool4 = new PoolBuilder()
                .register(new ItemEntry(Item.EXPERIENCE_BOTTLE, 7))
                .register(new ItemEntry(Item.STRING, 0, 6, 4))
                .register(new ItemEntry(Item.ARROW, 0, 7, 2, 4))
                .register(new ItemEntry(Item.TRIPWIRE_HOOK, 0, 3, 3))
                .register(new ItemEntry(Item.IRON_INGOT, 0, 3, 3))
                .register(new ItemEntry(Item.ENCHANT_BOOK, 1)); //TODO: enchant_randomly
        this.pools.put(pool4.build(), new RollEntry(3, 2, pool4.getTotalWeight()));
    }

    public static PillagerOutpostChest get() {
        return INSTANCE;
    }
}
