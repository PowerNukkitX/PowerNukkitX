package cn.nukkit.item;

public class ItemWitherSkeletonSpawnEgg extends ItemSpawnEgg {
    public ItemWitherSkeletonSpawnEgg() {
        super(WITHER_SKELETON_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 48;
    }

    @Override
    public void setDamage(int meta) {

    }
}