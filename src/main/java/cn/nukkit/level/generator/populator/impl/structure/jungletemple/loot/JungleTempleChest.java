package cn.nukkit.level.generator.populator.impl.structure.jungletemple.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.21-r6")
public class JungleTempleChest extends RandomizableContainer {

    private static final JungleTempleChest INSTANCE = new JungleTempleChest();

    public static JungleTempleChest get() {
        return INSTANCE;
    }

    private JungleTempleChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.DIAMOND, 0, 3, 1, 15))
                .register(new ItemEntry(Item.IRON_INGOT, 0, 5, 1, 50))
                .register(new ItemEntry(Item.GOLD_INGOT, 0, 7, 2, 75))
                .register(new ItemEntry(Item.EMERALD, 0, 3, 1, 10))
                .register(new ItemEntry(Item.BONE, 0, 6, 4, 100))
                .register(new ItemEntry(Item.ROTTEN_FLESH, 0, 7, 3, 80))
                //.register(new ItemEntry(Item.BAMBOO, 0, 3, 1, 75))
                .register(new ItemEntry(Item.SADDLE, 15))
                .register(new ItemEntry(Item.IRON_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.GOLD_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.DIAMOND_HORSE_ARMOR, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 6)); //TODO: ench nbt
        this.pools.put(pool1.build(), new RollEntry(6, 2, pool1.getTotalWeight()));
    }
}
