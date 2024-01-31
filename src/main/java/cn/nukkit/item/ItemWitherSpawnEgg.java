package cn.nukkit.item;

public class ItemWitherSpawnEgg extends ItemSpawnEgg {
    public ItemWitherSpawnEgg() {
        super(WITHER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 52;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}