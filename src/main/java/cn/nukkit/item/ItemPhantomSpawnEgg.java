package cn.nukkit.item;

public class ItemPhantomSpawnEgg extends ItemSpawnEgg {
    public ItemPhantomSpawnEgg() {
        super(PHANTOM_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 58;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}