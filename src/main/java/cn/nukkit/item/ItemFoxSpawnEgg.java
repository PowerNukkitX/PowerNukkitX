package cn.nukkit.item;

public class ItemFoxSpawnEgg extends ItemSpawnEgg {
    public ItemFoxSpawnEgg() {
        super(FOX_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 121;
    }

    @Override
    public void setDamage(int meta) {

    }
}