package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.EntityFreezeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.network.protocol.types.EntityLink;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityHuman extends EntityHumanType {
    protected UUID uuid;
    protected byte[] rawUUID;
    protected Skin skin;

    public EntityHuman(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.6f;
    }

    @Override
    public float getLength() {
        return 0.6f;
    }

    @Override
    public float getHeight() {
        return 1.8f;
    }

    @Override
    public float getSwimmingHeight() {
        return getWidth();
    }

    @Override
    public float getEyeHeight() {
        return (float) (boundingBox.getMaxY() - boundingBox.getMinY() - 0.18);
    }

    /**
     * 偏移客户端传输玩家位置的y轴误差
     *
     * @return the base offset
     */
    @Override
    protected float getBaseOffset() {
        return 1.62f;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    public void setRawUniqueId(byte[] rawUUID) {
        this.rawUUID = rawUUID;
    }

    public byte[] getRawUniqueId() {
        return rawUUID;
    }

    @Override
    protected void initEntity() {
        initHumanEntity(this);
        super.initEntity();
    }

    @Override
    public String getOriginalName() {
        return "Human";
    }

    @Override
    @NotNull public String getName() {
        return this.getNameTag();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        saveHumanEntity(this);
    }

    @Override
    public boolean entityBaseTick() {
        return this.entityBaseTick(1);
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);
        //handle human entity freeze
        var collidedWithPowderSnow = this.getTickCachedCollisionBlocks().stream().anyMatch(block -> block.getId() == Block.POWDER_SNOW);
        if (this.getFreezingTicks() < 140 && collidedWithPowderSnow) {
            if (getFreezingTicks() == 0) {//玩家疾跑进来要设置为非疾跑，统一为默认速度0.1
                this.setSprinting(false);
            }
            this.addFreezingTicks(1);
            EntityFreezeEvent event = new EntityFreezeEvent(this);
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.setMovementSpeed((float) Math.max(0.05, getMovementSpeed() - 3.58e-4));
            }
        } else if (this.getFreezingTicks() > 0 && !collidedWithPowderSnow) {
            this.addFreezingTicks(-1);
            this.setMovementSpeed((float) Math.min(Player.DEFAULT_SPEED, getMovementSpeed() + 3.58e-4));//This magic number is to change the player's 0.05 speed within 140tick
        }
        if (this.getFreezingTicks() == 140 && this.getServer().getTick() % 40 == 0) {
            this.attack(new EntityDamageEvent(this, EntityDamageEvent.DamageCause.FREEZING, getFrostbiteInjury()));
        }
        return hasUpdate;
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.level.addPlayerMovement(this, x, y, z, yaw, pitch, headYaw);
    }

    @Override
    public void spawnTo(Player player) {
        if (this != player && !this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);

            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (this instanceof Player)
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), ((Player) this).getDisplayName(), this.skin, ((Player) this).getLoginChainData().getXUID(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, new Player[]{player});

            AddPlayerPacket pk = new AddPlayerPacket();
            pk.uuid = this.getUniqueId();
            pk.username = this.getName();
            pk.entityUniqueId = this.getId();
            pk.entityRuntimeId = this.getId();
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.speedX = (float) this.motionX;
            pk.speedY = (float) this.motionY;
            pk.speedZ = (float) this.motionZ;
            pk.yaw = (float) this.yaw;
            pk.pitch = (float) this.pitch;
            pk.item = this.getInventory().getItemInHand();
            pk.entityData = this.entityDataMap;
            player.dataPacket(pk);

            this.inventory.sendArmorContents(player);
            this.offhandInventory.sendContents(player);

            if (this.riding != null) {
                SetEntityLinkPacket pkk = new SetEntityLinkPacket();
                pkk.vehicleUniqueId = this.riding.getId();
                pkk.riderUniqueId = this.getId();
                pkk.type = EntityLink.Type.RIDER;
                pkk.immediate = 1;

                player.dataPacket(pkk);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), player);
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {

            RemoveEntityPacket pk = new RemoveEntityPacket();
            pk.eid = this.getId();
            player.dataPacket(pk);
            this.hasSpawned.remove(player.getLoaderId());
        }
    }

    @Override
    public void close() {
        if (!this.closed) {
            if (inventory != null && (!(this instanceof Player) || ((Player) this).loggedIn)) {
                for (Player viewer : this.inventory.getViewers()) {
                    viewer.removeWindow(this.inventory);
                }
            }

            super.close();
        }
    }

    @Override
    protected void onBlock(Entity entity, EntityDamageEvent event, boolean animate) {
        super.onBlock(entity, event, animate);
        Item shield = getInventory().getItemInHand();
        Item shieldOffhand = getOffhandInventory().getItem(0);
        if (shield instanceof ItemShield) {
            shield = damageArmor(shield, entity, event);
            getInventory().setItemInHand(shield);
        } else if (shieldOffhand instanceof ItemShield) {
            shieldOffhand = damageArmor(shieldOffhand, entity, event);
            getOffhandInventory().setItem(0, shieldOffhand);
        }
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }
}
