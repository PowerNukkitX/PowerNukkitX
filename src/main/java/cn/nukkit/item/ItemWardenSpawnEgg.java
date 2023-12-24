package cn.nukkit.item;

public class ItemWardenSpawnEgg extends ItemSpawnEgg {
    public ItemWardenSpawnEgg() {
        super(WARDEN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 131;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}