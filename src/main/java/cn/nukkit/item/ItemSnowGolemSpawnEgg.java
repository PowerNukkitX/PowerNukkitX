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
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}