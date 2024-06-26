package cn.nukkit.item;

public class ItemBreezeSpawnEgg extends ItemSpawnEgg {
     public ItemBreezeSpawnEgg() {
         super(BREEZE_SPAWN_EGG);
     }

    @Override
    public int getEntityNetworkId() {
        return 140;
    }

    @Override
    public void setDamage(int meta) {

    }
}