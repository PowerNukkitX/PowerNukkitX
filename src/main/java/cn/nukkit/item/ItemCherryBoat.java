package cn.nukkit.item;

public class ItemCherryBoat extends ItemBoat {
    public ItemCherryBoat() {
        super(CHERRY_BOAT);
    }

    @Override
    public int getBoatId() {
        return 8;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}