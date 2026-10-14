package org.powernukkitx.item;

public class ItemPoppedChorusFruit extends Item {
    public ItemPoppedChorusFruit() {
        super(POPPED_CHORUS_FRUIT);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"minecraft:chorus_fruit_popped"};
    }
}