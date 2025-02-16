package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.CoreMemoryTypes;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityAnimal extends EntityIntelligent {
    public EntityAnimal(IChunk chunk, CompoundTag nbt) {
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
            return useBreedingItem(player, item) && superResult;
        }
        return superResult;
    }

    protected boolean useBreedingItem(Player player, Item item) {
        getMemoryStorage().put(CoreMemoryTypes.LAST_FEED_PLAYER, player);
        getMemoryStorage().put(CoreMemoryTypes.LAST_BE_FEED_TIME, getLevel().getTick());
        sendBreedingAnimation(item);
        item.count--;
        return player.getInventory().setItemInHand(item);
    }

    protected void sendBreedingAnimation(Item item) {
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.EATING_ITEM;
        pk.eid = this.getId();
        pk.data =  item.getFullId();
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
        return Objects.equals(item.getId(), BlockID.WHEAT); //default
    }

    @Override
    protected double getStepHeight() {
        return 0.5;
    }

    @Override
    public Integer getExperienceDrops() {
        return ThreadLocalRandom.current().nextInt(3) + 1;
    }

}
