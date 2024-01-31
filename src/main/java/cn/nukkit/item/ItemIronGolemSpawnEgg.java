package cn.nukkit.item;

public class ItemIronGolemSpawnEgg extends ItemSpawnEgg {
    public ItemIronGolemSpawnEgg() {
        super(IRON_GOLEM_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 20;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}