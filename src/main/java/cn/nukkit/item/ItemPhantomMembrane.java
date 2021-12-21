package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("FUTURE")
public class ItemPhantomMembrane extends Item {

    @Since("FUTURE")
    public ItemPhantomMembrane() {
        this(0, 1);
    }

    @Since("FUTURE")
    public ItemPhantomMembrane(Integer meta) {
        this(meta, 1);
    }

    @Since("FUTURE")
    public ItemPhantomMembrane(Integer meta, int count) {
        super(PHANTOM_MEMBRANE, meta, count, "Phantom Membrane");
    }
}
