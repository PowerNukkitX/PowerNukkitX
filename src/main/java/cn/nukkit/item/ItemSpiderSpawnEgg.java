package cn.nukkit.item;

public class ItemSpiderSpawnEgg extends ItemSpawnEgg {
    public ItemSpiderSpawnEgg() {
        super(SPIDER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 35;
    }

    @Override
    public void setDamage(int meta) {

    }
}