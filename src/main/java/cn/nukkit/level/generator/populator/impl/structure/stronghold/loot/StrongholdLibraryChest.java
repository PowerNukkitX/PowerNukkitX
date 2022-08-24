package cn.nukkit.level.generator.populator.impl.structure.stronghold.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

//\\ ./data/behavior_packs/vanilla/loot_tables/chests/stronghold_library.json
@PowerNukkitXOnly
@Since("1.19.21-r2")
public class StrongholdLibraryChest extends RandomizableContainer {

    private static final StrongholdLibraryChest INSTANCE = new StrongholdLibraryChest();

    public static StrongholdLibraryChest get() {
        return INSTANCE;
    }

    private StrongholdLibraryChest() {
        super(Maps.newHashMap(), InventoryType.CHEST.getDefaultSize());

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.BOOK, 0, 3, 100))
                .register(new ItemEntry(Item.PAPER, 0, 7, 2, 100))
                .register(new ItemEntry(Item.MAP, 5)) //TODO: EMPTY_MAP
                .register(new ItemEntry(Item.COMPASS, 5))
                .register(new ItemEntry(Item.ENCHANTED_BOOK, 60)); //TODO: treasure enchant_with_levels 30
        this.pools.put(pool1.build(), new RollEntry(10, 2, pool1.getTotalWeight()));
    }
}
