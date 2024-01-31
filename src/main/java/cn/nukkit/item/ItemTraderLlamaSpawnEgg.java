package cn.nukkit.item;

public class ItemTraderLlamaSpawnEgg extends ItemSpawnEgg {
    public ItemTraderLlamaSpawnEgg() {
        super(TRADER_LLAMA_SPAWN_EGG);
    }

    @Override
    public int getEntityNetworkId() {
        return 157;
    }

    @Override
    public void setDamage(int meta) {

    }
}