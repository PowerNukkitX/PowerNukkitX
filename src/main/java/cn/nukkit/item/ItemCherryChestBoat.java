package cn.nukkit.item;

public class ItemCherryChestBoat extends ItemChestBoat {
    public ItemCherryChestBoat() {
        super(CHERRY_CHEST_BOAT);
    }

    @Override
    public int getBoatId() {
        return 8;
    }

    @Override
    public void setDamage(int meta) {

    }
}