package cn.nukkit.item;

public class ItemArmadilloSpawnEgg extends ItemSpawnEgg {
     public ItemArmadilloSpawnEgg() {
         super(ARMADILLO_SPAWN_EGG);
     }

    @Override
    public int getEntityNetworkId() {
        return 142;
    }

    @Override
    public void setDamage(int meta) {

    }
}