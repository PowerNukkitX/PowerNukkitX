package cn.nukkit.item.randomitem;


import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

@PowerNukkitXOnly
@Since("1.19.40-r4")
public class ItemEchoShard extends Item {
    public ItemEchoShard() {
        this(0, 1);
    }

    public ItemEchoShard(Integer meta) {
        this(meta, 1);
    }

    public ItemEchoShard(Integer meta, int count) {
        super(ECHO_SHARD, meta, count, "Echo Shard");
    }
}
