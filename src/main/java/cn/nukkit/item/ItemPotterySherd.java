package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.20.40-r2")
public abstract class ItemPotterySherd extends Item {

    public ItemPotterySherd(int id) {
        this(id, 0, 1, UNKNOWN_STR);
    }

    public ItemPotterySherd(int id, Integer meta) {
        this(id, meta, 1, UNKNOWN_STR);
    }

    public ItemPotterySherd(int id, Integer meta, int count) {
        this(id, meta, count, UNKNOWN_STR);
    }

    public ItemPotterySherd(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

}
