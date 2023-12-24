package cn.nukkit.item;

public class ItemMuleSpawnEgg extends ItemSpawnEgg {
    public ItemMuleSpawnEgg() {
        super(MULE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 25;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}