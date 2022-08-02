package cn.nukkit.item;

public class ItemChestBoatAcacia extends ItemChestBoatBase {
    public ItemChestBoatAcacia() {
        this(0, 1);
    }

    public ItemChestBoatAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemChestBoatAcacia(Integer meta, int count) {
        this(ACACIA_CHEST_BOAT, meta, count, "Acacia Chest Boat");
    }

    protected ItemChestBoatAcacia(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 4;
    }
}
