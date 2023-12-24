package cn.nukkit.item;

public class ItemSnowGolemSpawnEgg extends ItemSpawnEgg {
    public ItemSnowGolemSpawnEgg() {
        super(SNOW_GOLEM_SPAWN_EGG);
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