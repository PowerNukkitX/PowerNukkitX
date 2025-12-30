package cn.nukkit.item;

public class ItemCopperGolemSpawnEgg extends ItemSpawnEgg {
    public ItemCopperGolemSpawnEgg() {
        super(COPPER_GOLEM_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 148;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}