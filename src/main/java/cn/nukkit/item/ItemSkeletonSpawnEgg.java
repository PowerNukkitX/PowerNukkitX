package cn.nukkit.item;

public class ItemSkeletonSpawnEgg extends ItemSpawnEgg {
    public ItemSkeletonSpawnEgg() {
        super(SKELETON_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 34;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}