package cn.nukkit.item;

public class ItemMooshroomSpawnEgg extends ItemSpawnEgg {
    public ItemMooshroomSpawnEgg() {
        super(MOOSHROOM_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 16;
    }

    @Override
    public void setDamage(int meta) {

    }
}