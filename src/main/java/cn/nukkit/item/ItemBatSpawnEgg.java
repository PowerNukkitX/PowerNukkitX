package cn.nukkit.item;

public class ItemBatSpawnEgg extends ItemSpawnEgg {
    public ItemBatSpawnEgg() {
        super(BAT_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 19;
    }

    @Override
    public void setDamage(int meta) {

    }
}