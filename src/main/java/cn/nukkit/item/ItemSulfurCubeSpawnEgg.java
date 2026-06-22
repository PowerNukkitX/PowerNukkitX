package cn.nukkit.item;

public class ItemSulfurCubeSpawnEgg extends ItemSpawnEgg {
    public ItemSulfurCubeSpawnEgg() {
        super(SULFUR_CUBE_SPAWN_EGG);
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    public int getEntityNetworkId() {
        return 153;
    }
}