package cn.nukkit.item;

public class ItemVillagerSpawnEgg extends ItemSpawnEgg {
    public ItemVillagerSpawnEgg() {
        super(VILLAGER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 115;
    }

    @Override
    public void setDamage(int meta) {

    }
}