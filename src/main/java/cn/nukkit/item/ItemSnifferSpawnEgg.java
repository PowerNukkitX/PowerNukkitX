package cn.nukkit.item;

public class ItemSnifferSpawnEgg extends ItemSpawnEgg {
    public ItemSnifferSpawnEgg() {
        super(SNIFFER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 139;
    }

    @Override
    public void setDamage(int meta) {

    }
}