package cn.nukkit.level.generator.populator.impl.structure.igloo.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class IglooChest extends RandomizableContainer {

    private static final IglooChest INSTANCE = new IglooChest();

    public static IglooChest get() {
        return INSTANCE;
    }

    private IglooChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.APPLE, 0, 3, 15))
                .register(new ItemEntry(Item.COAL, 0, 4, 15))
                .register(new ItemEntry(Item.GOLD_NUGGET, 0, 3, 10))
                .register(new ItemEntry(Item.STONE_AXE, 2))
                .register(new ItemEntry(Item.ROTTEN_FLESH, 10))
                .register(new ItemEntry(Item.EMERALD, 1))
                .register(new ItemEntry(Item.WHEAT, 0, 3, 2, 10));
        this.pools.put(pool1.build(), new RollEntry(8, 2, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.GOLDEN_APPLE, 1));
        this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
    }
}
