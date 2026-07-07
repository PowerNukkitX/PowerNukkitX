package org.powernukkitx.item;

public class ItemDonkeySpawnEgg extends ItemSpawnEgg {
    public ItemDonkeySpawnEgg() {
        super(DONKEY_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 24;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}