package cn.nukkit.item;

import cn.nukkit.api.Since;


public class ItemChorusFruitPopped extends Item {


    public ItemChorusFruitPopped() {
        this(0, 1);
    }


    public ItemChorusFruitPopped(Integer meta) {
        this(meta, 1);
    }


    public ItemChorusFruitPopped(Integer meta, int count) {
        super(POPPED_CHORUS_FRUIT, meta, count, "Popped Chorus Fruit");
    }
}
