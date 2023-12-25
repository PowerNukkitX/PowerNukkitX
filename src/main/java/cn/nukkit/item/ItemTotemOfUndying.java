package cn.nukkit.item;

public class ItemTotemOfUndying extends Item {
    public ItemTotemOfUndying() {
        super(TOTEM_OF_UNDYING);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}