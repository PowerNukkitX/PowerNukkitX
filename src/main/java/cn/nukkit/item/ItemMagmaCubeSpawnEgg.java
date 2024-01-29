package cn.nukkit.item;

public class ItemMagmaCubeSpawnEgg extends ItemSpawnEgg {
    public ItemMagmaCubeSpawnEgg() {
        super(MAGMA_CUBE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 42;
    }

    @Override
    public void setDamage(int meta) {

    }
}