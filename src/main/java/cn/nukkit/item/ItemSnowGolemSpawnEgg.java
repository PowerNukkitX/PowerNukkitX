package cn.nukkit.item;

public class ItemSnowGolemSpawnEgg extends ItemSpawnEgg {
    public ItemSnowGolemSpawnEgg() {
        super(SNOW_GOLEM_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 21;
    }

    @Override
    public void setDamage(int meta) {

    }
}