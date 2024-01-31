package cn.nukkit.item;

public class ItemStraySpawnEgg extends ItemSpawnEgg {
    public ItemStraySpawnEgg() {
        super(STRAY_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 46;
    }

    @Override
    public void setDamage(int meta) {

    }
}