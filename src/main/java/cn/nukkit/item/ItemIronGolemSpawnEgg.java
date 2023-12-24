package cn.nukkit.item;

public class ItemIronGolemSpawnEgg extends ItemSpawnEgg {
    public ItemIronGolemSpawnEgg() {
        super(IRON_GOLEM_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}