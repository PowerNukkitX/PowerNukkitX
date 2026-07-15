package org.powernukkitx.entity;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.AbilitiesIndex;
import org.cloudburstmc.protocol.bedrock.data.ActorLinkType;
import org.cloudburstmc.protocol.bedrock.data.BuildPlatform;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.PlayerPermissionLevel;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorLink;
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermissionLevel;
import org.cloudburstmc.protocol.bedrock.data.payload.abilities.SerializedAbilitiesData;
import org.cloudburstmc.protocol.bedrock.data.payload.abilities.SerializedAbilitiesDataSerializedLayer;
import org.cloudburstmc.protocol.bedrock.data.payload.abilities.SerializedLayer;
import org.cloudburstmc.protocol.bedrock.packet.AddPlayerPacket;
import org.cloudburstmc.protocol.bedrock.packet.RemoveActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorLinkPacket;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.entity.data.human.Skin;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.player.EntityFreezeEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemShield;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.SkinUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import static org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes.NAMEPLATE_RENDER_DISTANCE_MAX;
import static org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes.RESERVED_139;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Slf4j
public class EntityHuman extends EntityHumanType {
    public static final String GEOMETRY_CUSTOM = SkinUtils.convertGeometryName("geometry.humanoid.custom");
    public static final String GEOMETRY_CUSTOM_SLIM = SkinUtils.convertGeometryName("geometry.humanoid.customSlim");
    static final String GEOMETRY_HUMANOID;

    static {
        String geoData;
        try (var stream = EntityHuman.class.getClassLoader().getResourceAsStream("gamedata/skin_geometry.json");
             BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            geoData = reader.lines().collect(java.util.stream.Collectors.joining("\n", "", "\n"));
        } catch (IOException e) {
            geoData = "";
            log.error("Failed to load skin geometry data", e);
        }
        GEOMETRY_HUMANOID = geoData;
    }

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
    public void initHumanEntity(Entity ent) {
        super.initHumanEntity(ent);

        if(!(ent instanceof EntityHuman human)) return;


        if(!(human instanceof Player) && human.getSkin() != null) {
            org.cloudburstmc.protocol.bedrock.data.skin.Skin skin = human.getSkin().getSkin();
            boolean trusted = human.getSkin().isTrusted();
            org.cloudburstmc.protocol.bedrock.data.skin.Skin.Builder builder = human.getSkin().getSkin().toBuilder();

            boolean changed = false;

            if(skin.getSkinResourcePatch() == null || skin.getSkinResourcePatch().isEmpty()) {
                builder.skinResourcePatch(GEOMETRY_CUSTOM_SLIM);
                changed = true;
            } else {
                if(!SkinUtils.isValidResourcePatch(skin.getSkinResourcePatch())) {
                    builder.skinResourcePatch(GEOMETRY_CUSTOM_SLIM);
                    changed = true;
                }
            }

            if(skin.getGeometryData() == null || skin.getGeometryData().isEmpty()) {
                builder.skinResourcePatch(GEOMETRY_HUMANOID);
                changed = true;
            }

            if(changed)
                human.setSkin(new Skin(builder.build(), trusted));
        }
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


            if (!SkinUtils.isValid(this.skin.getSkin())) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

            if (this instanceof Player pl)
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), pl.getDisplayName(), this.skin, pl.getXUID(), pl.getLocatorBarColor(), new Player[]{player});
            else
                this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.skin, Color.WHITE, new Player[]{player});

            this.actorDataMap.put(RESERVED_139, 0L);
            this.actorDataMap.put(NAMEPLATE_RENDER_DISTANCE_MAX,  64.0f);

            final AddPlayerPacket addPlayerPacket = new AddPlayerPacket();
            addPlayerPacket.setActorData(this.actorDataMap);
            addPlayerPacket.setUuid(this.getUniqueId());
            addPlayerPacket.setPlayerName(this.getName());
            addPlayerPacket.setTargetActorID(this.getId());
            addPlayerPacket.setTargetRuntimeID(this.getId());
            addPlayerPacket.setPosition(this.getPosition().toNetwork());
            addPlayerPacket.setVelocity(this.getMotionVector());
            addPlayerPacket.setRotation(this.getRotationVector());
            addPlayerPacket.setCarriedItem(this.getInventory().getItemInMainHand().toNetwork());
            addPlayerPacket.setDeviceId("");
            addPlayerPacket.setPlatformChatId("");
            addPlayerPacket.setBuildPlatform(BuildPlatform.UNKNOWN);
            addPlayerPacket.setPlayerGameType(GameType.SURVIVAL);
            addPlayerPacket.setAbilitiesData(
                    this instanceof Player asPlayer ? asPlayer.getAdventureSettings().buildSerializedAbilitiesData() :
                            this.buildSerializedAbilitiesData()
            );
            player.sendPacket(addPlayerPacket);

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
                player.sendPacket(setActorLinkPacket);
            }

            if (!(this instanceof Player)) {
                this.server.removePlayerListData(this.getUniqueId(), player);
            }
        }
    }

    public SerializedAbilitiesData buildSerializedAbilitiesData() {
        final SerializedAbilitiesData data = new SerializedAbilitiesData();
        data.setTargetPlayerRawId(this.getId());
        data.setCommandPermissions(CommandPermissionLevel.ANY);
        data.setPlayerPermissions(PlayerPermissionLevel.MEMBER);
        final SerializedAbilitiesDataSerializedLayer layer = new SerializedAbilitiesDataSerializedLayer();
        layer.setSerializedLayer(SerializedLayer.BASE);
        layer.getAbilitiesSet().addAll(List.of(AbilitiesIndex.values()));
        layer.setFlySpeed(Player.DEFAULT_FLY_SPEED);
        layer.setVerticalFlySpeed(1f);
        layer.setWalkSpeed(Player.DEFAULT_SPEED);
        return data;
    }

    @Override
    public void despawnFrom(Player player) {
        if (this.hasSpawned.containsKey(player.getLoaderId())) {
            final RemoveActorPacket packet = new RemoveActorPacket();
            packet.setTargetActorID(this.getId());
            player.sendPacket(packet);
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
        Item shield = getInventory().getItemInMainHand();
        Item shieldOffhand = getOffhandInventory().getItem(0);
        if (shield instanceof ItemShield) {
            shield = damageArmor(shield, entity, event);
            getInventory().setItemInMainHand(shield);
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
