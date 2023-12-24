package cn.nukkit.item;

public class ItemParrotSpawnEgg extends ItemSpawnEgg {
    public ItemParrotSpawnEgg() {
        super(PARROT_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 30;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}