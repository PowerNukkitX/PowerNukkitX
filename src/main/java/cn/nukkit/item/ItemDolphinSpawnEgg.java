package cn.nukkit.item;

public class ItemDolphinSpawnEgg extends ItemSpawnEgg {
    public ItemDolphinSpawnEgg() {
        super(DOLPHIN_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 31;
    }

    @Override
    public void setDamage(int meta) {

    }
}