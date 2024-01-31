package cn.nukkit.item;

public class ItemSilverfishSpawnEgg extends ItemSpawnEgg {
    public ItemSilverfishSpawnEgg() {
        super(SILVERFISH_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 39;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}