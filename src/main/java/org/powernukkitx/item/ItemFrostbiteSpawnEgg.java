package org.powernukkitx.item;

public class ItemFrostbiteSpawnEgg extends ItemSpawnEgg {
    public ItemFrostbiteSpawnEgg() {
        super(FROSTBITE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 155;
    }

    @Override
    public void setDamage(int meta) {

    }
}
