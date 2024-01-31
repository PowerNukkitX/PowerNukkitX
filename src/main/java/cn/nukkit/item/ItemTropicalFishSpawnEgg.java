package cn.nukkit.item;

public class ItemTropicalFishSpawnEgg extends ItemSpawnEgg {
    public ItemTropicalFishSpawnEgg() {
        super(TROPICAL_FISH_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 111;
    }

    @Override
    public void setDamage(int meta) {

    }
}