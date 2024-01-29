package cn.nukkit.item;

public class ItemLlamaSpawnEgg extends ItemSpawnEgg {
    public ItemLlamaSpawnEgg() {
        super(LLAMA_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 29;
    }

    @Override
    public void setDamage(int meta) {

    }
}