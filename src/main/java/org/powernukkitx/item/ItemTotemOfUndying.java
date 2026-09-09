package org.powernukkitx.item;

public class ItemTotemOfUndying extends Item {
    public ItemTotemOfUndying() {
        super(TOTEM_OF_UNDYING);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"minecraft:totem"};
    }
}