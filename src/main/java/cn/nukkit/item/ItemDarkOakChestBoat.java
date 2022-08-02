package cn.nukkit.item;

public class ItemDarkOakChestBoat extends ItemChestBoatBase {
    public ItemDarkOakChestBoat() {
        this(0, 1);
    }

    public ItemDarkOakChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemDarkOakChestBoat(Integer meta, int count) {
        this(DARK_OAK_CHEST_BOAT, meta, count, "Dark Oak Chest Boat");
    }

    protected ItemDarkOakChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 0;
    }
}
