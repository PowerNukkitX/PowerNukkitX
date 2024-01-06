package cn.nukkit.item;

public class ItemSnifferSpawnEgg extends ItemSpawnEgg {
    public ItemSnifferSpawnEgg() {
        super(SNIFFER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}