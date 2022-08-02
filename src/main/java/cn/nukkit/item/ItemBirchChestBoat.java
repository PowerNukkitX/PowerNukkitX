package cn.nukkit.item;

public class ItemBirchChestBoat extends ItemChestBoatBase {
    public ItemBirchChestBoat() {
        this(0, 1);
    }

    public ItemBirchChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemBirchChestBoat(Integer meta, int count) {
        this(BIRCH_CHEST_BOAT, meta, count, "Birch Chest Boat");
    }

    protected ItemBirchChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 2;
    }
}
