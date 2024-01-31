package cn.nukkit.item;

public class ItemCodSpawnEgg extends ItemSpawnEgg {
    public ItemCodSpawnEgg() {
        super(COD_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 112;
    }

    @Override
    public void setDamage(int meta) {

    }
}