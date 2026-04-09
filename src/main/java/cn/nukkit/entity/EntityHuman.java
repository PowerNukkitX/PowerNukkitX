package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.EntityFreezeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.BuildPlatform;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorLink;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorLinkPacket;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityHuman extends EntityHumanType {
    protected UUID uuid;
    protected byte[] rawUUID;
    protected SerializedSkin skin;

    public EntityHuman(IChunk chunk, NbtMap nbt) {
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
        return 0.6f;
    }

    @Override
    public float getCrawlingHeight() {
        return 0.625f;
    }

    @Override
    public float getSneakingHeight() {
        return 1.5f;
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

    public SerializedSkin getSkin() {
        return skin;
    }

    public void setSkin(SerializedSkin skin) {
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
    @NotNull
    public String getName() {
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
        var collidedWithPowderSnow = this.getTickCachedCollisionBlocks().stream().anyMatch(block -> block.getId().equals(Block.POWDER_SNOW));
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
        if (this.getFreezingTicks() == 140 && this.getLevel().getTick() % 40 == 0) {
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
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), ((Player) this).getDisplayName(), this.skin, ((Player) this).getXUID(), ((Player) this).getLocatorBarColor(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, Color.WHITE, new Player[]{player});

            final AddPlayerPacket addPlayerPacket = new AddPlayerPacket();
            addPlayerPacket.setActorData(this.entityDataMap);
            addPlayerPacket.setUuid(this.getUniqueId());
            addPlayerPacket.setPlayerName(this.getName());
            addPlayerPacket.setTargetActorID(this.getId());
            addPlayerPacket.setTargetRuntimeID(this.getId());
            addPlayerPacket.setPosition(this.getPosition().toNetwork());
            addPlayerPacket.setVelocity(this.getMotionVector());
            addPlayerPacket.setRotation(this.getRotationVector());
            addPlayerPacket.setCarriedItem(this.getInventory().getItemInHand().toNetwork());
            addPlayerPacket.setDeviceId("");
            addPlayerPacket.setBuildPlatform(BuildPlatform.UNKNOWN);
            addPlayerPacket.setPlayerGameType(GameType.SURVIVAL);
            player.dataPacket(addPlayerPacket);

            this.inventory.sendArmorContents(player);
            this.offhandInventory.sendContents(player);

            if (this.riding != null) {
                final SetActorLinkPacket setActorLinkPacket = new SetActorLinkPacket();
                setActorLinkPacket.setLink(
                        new ActorLink(
                                this.riding.getId(),
                                this.getId(),
                                ActorLinkType.RIDING,
                                true,
                                false,
                                0f
                        )
                );
                player.dataPacket(setActorLinkPacket);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), player);
            }
        }
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            final RemoveActorPacket packet = new RemoveActorPacket();
            packet.setTargetActorID(this.getId());
            player.dataPacket(packet);
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

    protected Vector3f getMotionVector() {
        return Vector3f.from(this.motionX, this.motionY, this.motionZ);
    }

    protected Vector3f getRotationVector() {
        return Vector3f.from(this.pitch, this.yaw, this.headYaw);
    }
}
