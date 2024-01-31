package cn.nukkit.item;

public class ItemWolfSpawnEgg extends ItemSpawnEgg {
    public ItemWolfSpawnEgg() {
        super(WOLF_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 14;
    }

    @Override
    public void setDamage(int meta) {

    }
}