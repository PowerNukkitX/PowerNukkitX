package cn.nukkit.item;

import cn.nukkit.api.Since;


public class ItemPhantomMembrane extends Item {


    public ItemPhantomMembrane() {
        this(0, 1);
    }


    public ItemPhantomMembrane(Integer meta) {
        this(meta, 1);
    }


    public ItemPhantomMembrane(Integer meta, int count) {
        super(PHANTOM_MEMBRANE, meta, count, "Phantom Membrane");
    }
}
