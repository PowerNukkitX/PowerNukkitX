package cn.nukkit.item;

public class ItemCaveSpiderSpawnEgg extends ItemSpawnEgg {
    public ItemCaveSpiderSpawnEgg() {
        super(CAVE_SPIDER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 40;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}