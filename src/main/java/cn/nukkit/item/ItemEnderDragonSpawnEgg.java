package cn.nukkit.item;

public class ItemEnderDragonSpawnEgg extends ItemSpawnEgg {
    public ItemEnderDragonSpawnEgg() {
        super(ENDER_DRAGON_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 53;
    }

    @Override
    public void setDamage(int meta) {

    }
}