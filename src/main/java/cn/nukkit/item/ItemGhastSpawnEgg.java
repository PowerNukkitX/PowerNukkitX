package cn.nukkit.item;

public class ItemGhastSpawnEgg extends ItemSpawnEgg {
    public ItemGhastSpawnEgg() {
        super(GHAST_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 41;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}