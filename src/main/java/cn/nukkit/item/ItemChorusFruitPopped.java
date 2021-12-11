package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemChorusFruitPopped extends Item {

    @Since("1.4.0.0-PN")
    public ItemChorusFruitPopped() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemChorusFruitPopped(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemChorusFruitPopped(Integer meta, int count) {
        super(POPPED_CHORUS_FRUIT, meta, count, "Popped Chorus Fruit");
    }
}
