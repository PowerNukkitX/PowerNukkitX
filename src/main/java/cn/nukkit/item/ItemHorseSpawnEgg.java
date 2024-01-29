package cn.nukkit.item;

public class ItemHorseSpawnEgg extends ItemSpawnEgg {
    public ItemHorseSpawnEgg() {
        super(HORSE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 23;
    }

    @Override
    public void setDamage(int meta) {

    }
}