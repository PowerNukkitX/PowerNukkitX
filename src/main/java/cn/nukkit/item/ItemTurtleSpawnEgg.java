package cn.nukkit.item;

public class ItemTurtleSpawnEgg extends ItemSpawnEgg {
    public ItemTurtleSpawnEgg() {
        super(TURTLE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 74;
    }

    @Override
    public void setDamage(int meta) {

    }
}