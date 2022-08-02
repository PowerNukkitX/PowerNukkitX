package cn.nukkit.item;

public class ItemOakChestBoat extends ItemChestBoatBase {
    public ItemOakChestBoat() {
        this(0, 1);
    }

    public ItemOakChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemOakChestBoat(Integer meta, int count) {
        this(OAK_CHEST_BOAT, meta, count, "Oak Chest Boat");
    }

    protected ItemOakChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 5;
    }
}
