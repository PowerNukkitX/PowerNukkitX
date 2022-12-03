package cn.nukkit.level.generator.populator.impl.structure.oceanruin.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class RuinSmallChest extends RandomizableContainer {

    private static final RuinSmallChest INSTANCE = new RuinSmallChest();

    private RuinSmallChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.COAL, 0, 4, 10))
                .register(new ItemEntry(Item.STONE_AXE, 2))
                .register(new ItemEntry(Item.ROTTEN_FLESH, 5))
                .register(new ItemEntry(Item.EMERALD, 1))
                .register(new ItemEntry(Item.WHEAT, 0, 3, 2, 10));
        this.pools.put(pool1.build(), new RollEntry(8, 2, pool1.getTotalWeight()));

        PoolBuilder pool2 = new PoolBuilder()
                .register(new ItemEntry(Item.LEATHER_TUNIC, 1))
                .register(new ItemEntry(Item.GOLD_HELMET, 1))
                .register(new ItemEntry(Item.FISHING_ROD, 5)) //TODO: enchant_randomly
                .register(new ItemEntry(Item.MAP, 5)); //TODO: exploration_map buried treasure
        this.pools.put(pool2.build(), new RollEntry(1, pool2.getTotalWeight()));
    }

    public static RuinSmallChest get() {
        return INSTANCE;
    }
}
