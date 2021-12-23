package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("FUTURE")
public class ItemScute extends Item {

    @Since("FUTURE")
    public ItemScute() {
        this(0, 1);
    }

    @Since("FUTURE")
    public ItemScute(Integer meta) {
        this(meta, 1);
    }

    @Since("FUTURE")
    public ItemScute(Integer meta, int count) {
        super(SCUTE, meta, count, "Scute");
    }
}
