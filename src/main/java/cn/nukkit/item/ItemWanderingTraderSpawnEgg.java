package cn.nukkit.item;

public class ItemWanderingTraderSpawnEgg extends ItemSpawnEgg {
    public ItemWanderingTraderSpawnEgg() {
        super(WANDERING_TRADER_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 118;
    }

    @Override
    public void setDamage(int meta) {
        
    }
}