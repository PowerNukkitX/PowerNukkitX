package cn.nukkit.item;

public class ItemChestBoatCherry extends ItemChestBoatBase {
    public ItemChestBoatCherry() {
        this(0, 1);
    }

    public ItemChestBoatCherry(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatCherry(Integer meta, int count) {
        this(CHERRY_CHEST_BOAT, meta, count, "Cherry Chest Boat");
    }

    protected ItemChestBoatCherry(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 8;
    }
}
