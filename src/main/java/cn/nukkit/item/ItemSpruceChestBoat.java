package cn.nukkit.item;

public class ItemSpruceChestBoat extends ItemChestBoat {
    public ItemSpruceChestBoat() {
        super(SPRUCE_CHEST_BOAT);
    }

    @Override
    public int getBoatId() {
        return 1;
    }

    @Override
    public void setDamage(int meta) {

    }
}