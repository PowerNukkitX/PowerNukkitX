package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("FUTURE")
public class ItemHeartOfTheSea extends Item {

    @Since("FUTURE")
    public ItemHeartOfTheSea() {
        this(0, 1);
    }

    @Since("FUTURE")
    public ItemHeartOfTheSea(Integer meta) {
        this(meta, 1);
    }

    @Since("FUTURE")
    public ItemHeartOfTheSea(Integer meta, int count) {
        super(HEART_OF_THE_SEA, meta, count, "Heart Of The Sea");
    }
}
