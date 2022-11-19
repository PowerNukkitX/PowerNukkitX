package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityAnimal extends EntityIntelligent {
    public EntityAnimal(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (isBreedingItem(item)) {
            getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
            sendBreedingAnimation(item);
            item.count--;
            return player.getInventory().setItemInHand(item) && superResult;
        }
        return superResult;
    }

    protected void sendBreedingAnimation(Item item) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.EATING_ITEM;
        pk.eid = this.getId();
        pk.data = RuntimeItems.getFullId(item.getNetworkId(), item.getDamage());
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    /**
     * 可以导致繁殖的喂养物品
     * <p>
     * Feeding items that can lead to reproduction.
     *
     * @param item 物品
     * @return boolean 是否可以导致繁殖<br>Whether it can lead to reproduction
     */
    public boolean isBreedingItem(Item item) {
        return item.getId() == Item.WHEAT; //default
    }

    @Override
    protected double getStepHeight() {
        return 0.5;
    }
}
