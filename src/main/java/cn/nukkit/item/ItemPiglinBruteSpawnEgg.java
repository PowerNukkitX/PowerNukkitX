package cn.nukkit.item;

public class ItemPiglinBruteSpawnEgg extends ItemSpawnEgg {
    public ItemPiglinBruteSpawnEgg() {
        super(PIGLIN_BRUTE_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 127;
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}