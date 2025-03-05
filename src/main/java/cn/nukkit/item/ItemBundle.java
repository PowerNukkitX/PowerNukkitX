package cn.nukkit.item;

public class ItemBundle extends Item {

    public ItemBundle() {
        this(BUNDLE);
    }

    public ItemBundle(String id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}

