package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityAgeable;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.PlayerBreedingMemory;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.event.entity.EntityEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.item.RuntimeItems;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

import static cn.nukkit.entity.passive.EntityTamable.DATA_OWNER_NAME;
import static cn.nukkit.entity.passive.EntityTamable.DATA_TAMED_FLAG;

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
        if (this instanceof EntityTamable){
            if (getDataProperty(DATA_TAMED_FLAG) == null) {
                setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) 0));
            }

            if (getDataProperty(DATA_OWNER_NAME) == null) {
                setDataProperty(new StringEntityData(DATA_OWNER_NAME, ""));
            }

            String ownerName = "";

            if (namedTag != null) {
                if (namedTag.contains("Owner")) {
                    ownerName = namedTag.getString("Owner");
                }

                if (ownerName.length() > 0) {
                    this.setOwnerName(ownerName);
                    this.setTamed(true);
                }

                this.setSitting(namedTag.getBoolean("Sitting"));
            }
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        if (this instanceof EntityTamable){
            if (this.getOwnerName() == null) {
                namedTag.putString("Owner", "");
            } else {
                namedTag.putString("Owner", getOwnerName());
            }

            namedTag.putBoolean("Sitting", isSitting());
        }
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        boolean superResult = super.onInteract(player, item, clickedPos);
        if (isBreedingItem(item)){
            getMemoryStorage().get(PlayerBreedingMemory.class).setData(player);
            sendBreedingAnimation(item);
            item.count--;
            return player.getInventory().setItemInHand(item) && superResult;
        }
        return superResult;
    }

    protected void sendBreedingAnimation(Item item){
        EntityEventPacket pk = new EntityEventPacket();
        pk.event = EntityEventPacket.EATING_ITEM;
        pk.eid = this.getId();
        pk.data = RuntimeItems.getFullId(item.getNetworkId(),item.getDamage());
        Server.broadcastPacket(this.getViewers().values(), pk);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public String getOwnerName() {
        return getDataPropertyString(DATA_OWNER_NAME);
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setOwnerName(String playerName) {
        setDataProperty(new StringEntityData(DATA_OWNER_NAME, playerName));
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public Player getOwner() {
        return getServer().getPlayer(getOwnerName());
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean isTamed() {
        return (getDataPropertyByte(DATA_TAMED_FLAG) & 4) != 0;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setTamed(boolean flag) {
        int var = getDataPropertyByte(DATA_TAMED_FLAG); // ?

        if (flag) {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) (var | 4)));
        } else {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) (var & -5)));
        }
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public boolean isSitting() {
        return (getDataPropertyByte(DATA_TAMED_FLAG) & 1) != 0;
    }

    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public void setSitting(boolean flag) {
        int var = getDataPropertyByte(DATA_TAMED_FLAG); // ?

        if (flag) {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) (var | 1)));
        } else {
            setDataProperty(new ByteEntityData(DATA_TAMED_FLAG, (byte) (var & -2)));
        }
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
