package cn.nukkit.item;

public class ItemJungleChestBoat extends ItemChestBoatBase {
    public ItemJungleChestBoat() {
        this(0, 1);
    }

    public ItemJungleChestBoat(Integer meta) {
        this(meta, 1);
    }

    public ItemJungleChestBoat(Integer meta, int count) {
        this(JUNGLE_CHEST_BOAT, meta, count, "Jungle Chest Boat");
    }

    protected ItemJungleChestBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public int getBoatId() {
        return 3;
    }
}