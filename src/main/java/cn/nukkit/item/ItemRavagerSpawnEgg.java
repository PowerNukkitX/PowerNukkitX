package cn.nukkit.item;

public class ItemRavagerSpawnEgg extends ItemSpawnEgg {
    public ItemRavagerSpawnEgg() {
        super(RAVAGER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 59;
    }

    @Override
    public void setDamage(int meta) {

    }
}