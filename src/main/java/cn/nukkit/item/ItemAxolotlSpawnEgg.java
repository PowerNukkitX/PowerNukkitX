package cn.nukkit.item;

public class ItemAxolotlSpawnEgg extends ItemSpawnEgg {
    public ItemAxolotlSpawnEgg() {
        super(AXOLOTL_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 130;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}