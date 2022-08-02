package cn.nukkit.item;

public class ItemChestBoatBirch extends ItemChestBoatBase {
    public ItemChestBoatBirch() {
        this(0, 1);
    }

    public ItemChestBoatBirch(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatBirch(Integer meta, int count) {
        this(BIRCH_CHEST_BOAT, meta, count, "Birch Chest Boat");
    }

    protected ItemChestBoatBirch(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 2;
    }
}
