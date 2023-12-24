package cn.nukkit.item;

public class ItemBambooChestRaft extends ItemChestBoat {
    public ItemBambooChestRaft() {
        super(BAMBOO_CHEST_RAFT);
    }

    @Override
    public int getBoatId() {
        return 7;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}