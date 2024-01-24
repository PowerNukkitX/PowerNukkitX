package cn.nukkit.item;

public class ItemAgentSpawnEgg extends ItemSpawnEgg {
    public ItemAgentSpawnEgg() {
        super(AGENT_SPAWN_EGG);
    }

    @Override
    public void setDamage(Integer meta) {
        
    }

    @Override
    public int getEntityNetworkId() {
        return 56;
    }
}