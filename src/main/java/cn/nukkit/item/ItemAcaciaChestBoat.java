package cn.nukkit.item;

public class ItemAcaciaChestBoat extends ItemChestBoatBase {
    public ItemAcaciaChestBoat() {
        this(0, 1);
    }

    public ItemAcaciaChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemAcaciaChestBoat(Integer meta, int count) {
        this(ACACIA_CHEST_BOAT, meta, count, "Acacia Chest Boat");
    }

    protected ItemAcaciaChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 5;
    }
}
