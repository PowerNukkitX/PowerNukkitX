package cn.nukkit.item;

public class ItemSpruceChestBoat extends ItemChestBoatBase {
    public ItemSpruceChestBoat() {
        this(0, 1);
    }

    public ItemSpruceChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemSpruceChestBoat(Integer meta, int count) {
        this(SPRUCE_CHEST_BOAT, meta, count, "Spruce Chest Boat");
    }

    protected ItemSpruceChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 4;
    }
}
