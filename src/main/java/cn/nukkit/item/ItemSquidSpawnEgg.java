package cn.nukkit.item;

public class ItemSquidSpawnEgg extends ItemSpawnEgg {
    public ItemSquidSpawnEgg() {
        super(SQUID_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 17;
    }

    @Override
    public void setDamage(int meta) {

    }
}