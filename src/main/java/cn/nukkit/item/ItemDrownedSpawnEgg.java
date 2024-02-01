package cn.nukkit.item;

public class ItemDrownedSpawnEgg extends ItemSpawnEgg {
    public ItemDrownedSpawnEgg() {
        super(DROWNED_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 110;
    }

    @Override
    public void setDamage(int meta) {
    }
}