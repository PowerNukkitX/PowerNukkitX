package cn.nukkit.item;

public class ItemPillagerSpawnEgg extends ItemSpawnEgg {
    public ItemPillagerSpawnEgg() {
        super(PILLAGER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 114;
    }

    @Override
    public void setDamage(int meta) {

    }
}