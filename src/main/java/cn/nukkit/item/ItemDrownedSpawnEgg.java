package cn.nukkit.item;

public class ItemDrownedSpawnEgg extends ItemSpawnEgg {
    public ItemDrownedSpawnEgg() {
        super(DROWNED_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 132;
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}