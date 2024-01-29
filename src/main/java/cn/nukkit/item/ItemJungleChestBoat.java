package cn.nukkit.item;

public class ItemJungleChestBoat extends ItemChestBoat {
    public ItemJungleChestBoat() {
        super(JUNGLE_CHEST_BOAT);
    }

    @Override
    public int getBoatId() {
        return 3;
    }

    @Override
    public void setDamage(int meta) {

    }
}