package cn.nukkit.item;

public class ItemSalmonSpawnEgg extends ItemSpawnEgg {
    public ItemSalmonSpawnEgg() {
        super(SALMON_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 109;
    }

    @Override
    public void setDamage(int meta) {

    }
}