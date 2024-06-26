package cn.nukkit.item;

public class ItemBoggedSpawnEgg extends ItemSpawnEgg {
     public ItemBoggedSpawnEgg() {
         super(BOGGED_SPAWN_EGG);
     }

    @Override
    public int getEntityNetworkId() {
        return 144;
    }

    @Override
    public void setDamage(int meta) {

    }
}