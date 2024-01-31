package cn.nukkit.item;

public class ItemBirchChestBoat extends ItemChestBoat {
    public ItemBirchChestBoat() {
        super(BIRCH_CHEST_BOAT);
    }

    @Override
    public int getBoatId() {
        return 2;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}