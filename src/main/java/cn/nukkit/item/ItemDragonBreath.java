package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemDragonBreath extends Item {

    @Since("1.4.0.0-PN")
    public ItemDragonBreath() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemDragonBreath(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemDragonBreath(Integer meta, int count) {
        super(DRAGON_BREATH, meta, count, "Dragon's Breath");
    }
}
