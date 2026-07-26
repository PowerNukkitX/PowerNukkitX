package org.powernukkitx.item;

public class ItemNpcSpawnEgg extends ItemSpawnEgg {
    public ItemNpcSpawnEgg() {
        super(NPC_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 51;
    }

    @Override
    public void setDamage(int meta) {

    }
}