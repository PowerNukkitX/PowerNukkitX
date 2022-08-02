package cn.nukkit.item;

public class ItemMangroveChestBoat extends ItemChestBoatBase {
    public ItemMangroveChestBoat() {
        this(0, 1);
    }

    public ItemMangroveChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemMangroveChestBoat(Integer meta, int count) {
        this(MANGROVE_CHEST_BOAT, meta, count, "Mangrove Chest Boat");
    }

    protected ItemMangroveChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 6;
    }
}
