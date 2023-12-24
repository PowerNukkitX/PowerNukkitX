package cn.nukkit.item;

public class ItemBirchBoat extends ItemBoat {
    public ItemBirchBoat() {
        super(BIRCH_BOAT);
    }

    @Override
    public int getBoatId() {
        return 2;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}