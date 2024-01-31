package cn.nukkit.item;

public class ItemGlowSquidSpawnEgg extends ItemSpawnEgg {
    public ItemGlowSquidSpawnEgg() {
        super(GLOW_SQUID_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 129;
    }

    @Override
    public void setDamage(int meta) {

    }
}