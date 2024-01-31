package cn.nukkit.item;

public class ItemStriderSpawnEgg extends ItemSpawnEgg {
    public ItemStriderSpawnEgg() {
        super(STRIDER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 125;
    }

    @Override
    public void setDamage(int meta) {

    }
}