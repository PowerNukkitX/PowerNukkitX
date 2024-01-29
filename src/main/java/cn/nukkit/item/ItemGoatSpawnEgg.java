package cn.nukkit.item;

public class ItemGoatSpawnEgg extends ItemSpawnEgg {
    public ItemGoatSpawnEgg() {
        super(GOAT_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 128;
    }

    @Override
    public void setDamage(int meta) {

    }
}