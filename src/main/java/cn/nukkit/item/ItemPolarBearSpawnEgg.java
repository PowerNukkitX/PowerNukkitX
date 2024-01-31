package cn.nukkit.item;

public class ItemPolarBearSpawnEgg extends ItemSpawnEgg {
    public ItemPolarBearSpawnEgg() {
        super(POLAR_BEAR_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 28;
    }

    @Override
    public void setDamage(int meta) {

    }
}