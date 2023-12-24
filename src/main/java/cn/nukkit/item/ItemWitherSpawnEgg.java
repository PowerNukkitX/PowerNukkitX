package cn.nukkit.item;

public class ItemWitherSpawnEgg extends ItemSpawnEgg {
    public ItemWitherSpawnEgg() {
        super(WITHER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}