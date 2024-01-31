package cn.nukkit.item;

public class ItemVindicatorSpawnEgg extends ItemSpawnEgg {
    public ItemVindicatorSpawnEgg() {
        super(VINDICATOR_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 57;
    }

    @Override
    public void setDamage(int meta) {

    }
}