package cn.nukkit.item;

public class ItemSkeletonHorseSpawnEgg extends ItemSpawnEgg {
    public ItemSkeletonHorseSpawnEgg() {
        super(SKELETON_HORSE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 26;
    }

    @Override
    public void setDamage(int meta) {

    }
}