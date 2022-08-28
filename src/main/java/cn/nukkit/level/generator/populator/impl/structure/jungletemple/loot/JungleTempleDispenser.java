package cn.nukkit.level.generator.populator.impl.structure.jungletemple.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.level.generator.populator.impl.structure.utils.loot.RandomizableContainer;
import com.google.common.collect.Maps;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class JungleTempleDispenser extends RandomizableContainer {

    private static final JungleTempleDispenser INSTANCE = new JungleTempleDispenser();

    public static JungleTempleDispenser get() {
        return INSTANCE;
    }

    private JungleTempleDispenser() {
        super(Maps.newHashMap(), 9); //InventoryType.DISPENSER.getDefaultSize()

        PoolBuilder pool1 = new PoolBuilder()
                .register(new ItemEntry(Item.ARROW, 0, 7, 2, 1));
        this.pools.put(pool1.build(), new RollEntry(2, 0));
    }
}
