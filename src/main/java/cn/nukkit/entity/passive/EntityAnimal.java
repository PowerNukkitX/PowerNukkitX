package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.PlayerBreedingMemory;
import cn.nukkit.item.Item;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EntityAnimal extends EntityIntelligent implements EntityAgeable {
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
            getMemoryStorage().get(PlayerBreedingMemory.class).setData(player);
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

    @Override
    public boolean isBaby() {
        return this.getDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setBaby(boolean flag) {
        this.setDataFlag(DATA_FLAGS, Entity.DATA_FLAG_BABY, flag);
        if (flag)
            this.setScale(0.5f);
        else
            this.setScale(1f);
    }

    public boolean isBreedingItem(Item item) {
        return item.getId() == Item.WHEAT; //default
    }

    @Override
    protected double getStepHeight() {
        return 0.5;
    }
}
