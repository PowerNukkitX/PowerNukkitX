package org.powernukkitx.entity.item;

import org.powernukkitx.Server;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.components.NameableComponent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.entity.EntityDamageEvent.DamageCause;
import org.powernukkitx.event.entity.ItemDespawnEvent;
import org.powernukkitx.event.entity.ItemSpawnEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.AddItemActorPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * @author MagicDroidX
 */
@Slf4j
public class EntityItem extends Entity {
    @Override
    @NotNull
    public String getIdentifier() {
        return ITEM;
    }

    private static final int VANILLA_DESPAWN_AGE = 6000;
    private static final int VANILLA_MERGE_INTERVAL = 25;
    private static final int INFINITE_PICKUP_DELAY = 65535;
    private static final String PNX_SHOULD_DESPAWN = "ShouldDespawn";
    private static final String PNX_DISPLAY_ONLY = "DisplayOnly";
    private static final String PNX_MERGEABLE = "Mergeable";
    private static final String PNX_OWNER_NAME = "OwnerName";
    private static final String PNX_PICKUP_DELAY = "PickupDelay";

    protected long ownerId;
    protected String ownerName;
    protected Item item;
    protected int pickupDelay;
    private boolean mergeItems;
    private boolean shouldDespawn;
    private boolean isDisplayOnly;

    public EntityItem(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public float getDefaultGravity() {
        if (!getDataFlag(ActorFlags.HAS_GRAVITY)) return 0f;
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.125f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    // items use their own despawn system, so they should be persistent to prevent them from being unloaded when far away from players
    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public NameableComponent getComponentNameable() {
        return DEFAULT_NOT_NAMEABLE;
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        this.setHealthMax(5);

        final CompoundTag nbtMap = this.getNbt();
        this.setHealthCurrent(nbtMap.getShort("Health"));
        this.age = nbtMap.getShort("Age");
        this.ownerId = nbtMap.getLong("OwnerID");

        this.shouldDespawn = true;
        this.isDisplayOnly = false;
        this.mergeItems = true;
        this.pickupDelay = 0;

        CompoundTag customData = nbtMap.containsCompound(NBT_PNX_CUSTOM)
                ? nbtMap.getCompound(NBT_PNX_CUSTOM)
                : new CompoundTag();

        if (customData.containsNumber(PNX_SHOULD_DESPAWN)) {
            this.shouldDespawn = customData.getBoolean(PNX_SHOULD_DESPAWN);
        }

        if (customData.containsNumber(PNX_DISPLAY_ONLY)) {
            this.isDisplayOnly = customData.getBoolean(PNX_DISPLAY_ONLY);
        }

        if (customData.containsNumber(PNX_MERGEABLE)) {
            this.mergeItems = customData.getBoolean(PNX_MERGEABLE);
        }

        if (customData.containsString(PNX_OWNER_NAME)) {
            this.ownerName = customData.getString(PNX_OWNER_NAME);
        }

        if (customData.containsNumber(PNX_PICKUP_DELAY)) {
            this.pickupDelay = customData.getInt(PNX_PICKUP_DELAY);
        }

        if (this.isDisplayOnly) {
            this.pickupDelay = INFINITE_PICKUP_DELAY;
        }

        if (!nbtMap.containsCompound("Item")) {
            this.close();
            return;
        }

        this.item = ItemHelper.read(nbtMap.getCompound("Item"));
        this.actorDataMap.put(ActorDataTypes.OWNER, this.ownerId);
        this.setDataFlag(ActorFlags.HAS_GRAVITY, true);

        if (this.item.isLavaResistant()) {
            this.setFireImmune(true);
        }

        this.server.getPluginManager().callEvent(new ItemSpawnEvent(this));
    }


    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isDisplayOnly()) return false;

        if (item != null && item.isLavaResistant() && (
                source.getCause() == DamageCause.LAVA ||
                        source.getCause() == DamageCause.FIRE ||
                        source.getCause() == DamageCause.FIRE_TICK)) {
            return false;
        }

        return (source.getCause() == DamageCause.VOID ||
                source.getCause() == DamageCause.CONTACT ||
                source.getCause() == DamageCause.FIRE_TICK ||
                (source.getCause() == DamageCause.ENTITY_EXPLOSION ||
                        source.getCause() == DamageCause.BLOCK_EXPLOSION) &&
                        !this.isInsideOfWater() && (this.item == null ||
                        !Objects.equals(this.item.getId(), Item.NETHER_STAR))) && super.attack(source);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;

        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }

        this.lastUpdate = currentTick;

        if (this.canMergeItems() && this.age % VANILLA_MERGE_INTERVAL == 0 && this.onGround && this.getItem() != null && this.isAlive()) {
            if (this.getItem().getCount() < this.getItem().getMaxStackSize()) {
                for (EntityItem entity : this.getLevel().getCollidingItemEntities(getBoundingBox().grow(1, 1, 1))) {
                    if (entity != this) {
                        if (!entity.isAlive()) {
                            continue;
                        }
                        if (!entity.canMergeItems()) continue;
                        Item closeItem = entity.getItem();
                        if (!closeItem.equals(getItem(), true, true)) {
                            continue;
                        }
                        if (!entity.isOnGround()) {
                            continue;
                        }
                        int newAmount = this.getItem().getCount() + closeItem.getCount();
                        if (newAmount > this.getItem().getMaxStackSize()) {
                            continue;
                        }

                        this.pickupDelay = Math.max(this.pickupDelay, entity.pickupDelay);
                        this.age = Math.min(this.age, entity.age);

                        entity.close();
                        this.getItem().setCount(newAmount);

                        final ActorEventPacket packet = new ActorEventPacket();
                        packet.setTargetRuntimeID(this.runtimeId());
                        packet.setTargetRuntimeID(this.runtimeId());
                        packet.setType(ActorEvent.UPDATE_STACK_SIZE);
                        packet.setData(newAmount);
                        Server.broadcastPacket(this.getViewers().values(), packet);
                    }
                }
            }
        }

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        boolean lavaResistant = fireProof || item != null && item.isLavaResistant();

        if (!lavaResistant && (isInsideOfFire() || isInsideOfLava())) {
            this.kill();
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay != INFINITE_PICKUP_DELAY) {
                this.pickupDelay -= tickDiff;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            }/* else { // Done in Player#checkNearEntities
                for (Entity entity : this.level.getNearbyEntities(this.boundingBox.grow(1, 0.5, 1), this)) {
                    if (entity instanceof Player) {
                        if (((Player) entity).pickupEntity(this, true)) {
                            return true;
                        }
                    }
                }
            }*/

            String bid = this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0);
            if (this.inBubbleColumn) {
                hasUpdate = true;
            } else if (Objects.equals(bid, BlockID.FLOWING_WATER) || Objects.equals(bid, BlockID.WATER)
                    || Objects.equals(bid = this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.FLOWING_WATER)
                    || Objects.equals(bid, BlockID.WATER)
            ) {
                //item is fully in water or in still water
                this.motionY -= this.getGravity() * -0.015;
            } else if (lavaResistant && (
                    Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0), BlockID.FLOWING_LAVA)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 0), BlockID.LAVA)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.FLOWING_LAVA)
                            || Objects.equals(this.level.getBlockIdAt((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z, 1), BlockID.LAVA)
            )) {
                //item is fully in lava or in still lava
                this.motionY -= this.getGravity() * -0.015;
            } else if (this.isInsideOfWater() || lavaResistant && this.isInsideOfLava()) {
                this.motionY = this.getGravity() - 0.06; //item is going up in water, don't let it go back down too fast
            } else {
                this.motionY -= this.getGravity(); //item is not in water
            }

            if (this.checkObstruction(this.x, this.y, this.z)) {
                hasUpdate = true;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= this.getLevel().getBlock(this.temporalVector.setComponents((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z))).getFrictionFactor();
            }

            this.motionX *= friction;
            if (!this.inBubbleColumn) {
                this.motionY *= 1 - this.getDrag();
            }
            this.motionZ *= friction;

            if (this.onGround && !this.inBubbleColumn) {
                this.motionY *= -0.5;
            }

            this.updateMovement();

            if (this.age >= VANILLA_DESPAWN_AGE) {
                if (!this.shouldDespawn || this.isDisplayOnly) {
                    this.age = 0;
                    respawnToAll();
                } else {
                    ItemDespawnEvent ev = new ItemDespawnEvent(this);
                    this.server.getPluginManager().callEvent(ev);
                    if (ev.isCancelled()) {
                        this.age = 0;
                        respawnToAll();
                    } else {
                        this.kill();
                        hasUpdate = true;
                    }
                }
            }
        }

        return hasUpdate || !this.onGround || this.isInsideOfWaterPhysics()
                || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

    @Override
    public void setOnFire(int seconds) {
        if (this.isDisplayOnly()) return;
        if (item != null && item.isLavaResistant()) {
            return;
        }
        super.setOnFire(seconds);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.item == null) {
            return;
        }

        CompoundTag previousItem = this.nbt.containsCompound("Item")
                ? this.nbt.getCompound("Item")
                : null;

        this.nbt.putCompound("Item", ItemHelper.write(this.item, previousItem))
                .putShort("Health", (short) this.getHealthCurrent())
                .putShort("Age", (short) this.age)
                .putLong("OwnerID", this.ownerId);

        CompoundTag customData = this.nbt.containsCompound(NBT_PNX_CUSTOM)
                ? this.nbt.getCompound(NBT_PNX_CUSTOM).copy()
                : new CompoundTag();

        if (this.shouldDespawn) {
            customData.remove(PNX_SHOULD_DESPAWN);
        } else {
            customData.putBoolean(PNX_SHOULD_DESPAWN, false);
        }

        if (this.isDisplayOnly) {
            customData.putBoolean(PNX_DISPLAY_ONLY, true);
        } else {
            customData.remove(PNX_DISPLAY_ONLY);
        }

        if (this.mergeItems) {
            customData.remove(PNX_MERGEABLE);
        } else {
            customData.putBoolean(PNX_MERGEABLE, false);
        }

        if (this.ownerName == null || this.ownerName.isBlank()) {
            customData.remove(PNX_OWNER_NAME);
        } else {
            customData.putString(PNX_OWNER_NAME, this.ownerName);
        }

        if (!this.isDisplayOnly && this.pickupDelay != 0) {
            customData.putInt(PNX_PICKUP_DELAY, this.pickupDelay);
        } else {
            customData.remove(PNX_PICKUP_DELAY);
        }

        if (customData.isEmpty()) {
            this.nbt.remove(NBT_PNX_CUSTOM);
        } else {
            this.nbt.putCompound(NBT_PNX_CUSTOM, customData);
        }
    }

    @Override
    public String getOriginalName() {
        return "Item";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("item", "inanimate");
    }

    @Override
    @NotNull
    public String getName() {
        if (this.hasCustomName()) {
            return getNameTag();
        }
        if (item == null) {
            return getOriginalName();
        }
        return item.count + "x " + this.item.getDisplayName();
    }

    public Item getItem() {
        return item;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = this.isDisplayOnly
                ? INFINITE_PICKUP_DELAY
                : Math.max(0, pickupDelay);
    }

    public void setDisplayOnly(boolean isDisplayOnly) {
        this.isDisplayOnly = isDisplayOnly;

        if (isDisplayOnly) {
            this.pickupDelay = INFINITE_PICKUP_DELAY;
        } else if (this.pickupDelay == INFINITE_PICKUP_DELAY) {
            this.pickupDelay = 0;
        }
    }

    public boolean isDisplayOnly() {
        return isDisplayOnly;
    }

    /**
     * Returns whether this item entity may merge with compatible item entities.
     *
     * @return whether merging is enabled
     */
    public boolean isMergeable() {
        return this.mergeItems;
    }

    /**
     * Sets whether this item entity may merge with compatible item entities.
     *
     * @param mergeItems whether merging is enabled
     */
    public void setMergeable(boolean mergeItems) {
        this.mergeItems = mergeItems;
    }

    /**
     * Returns whether this item should despawn when its age limit is reached.
     *
     * @return whether the item should despawn
     */
    public boolean shouldDespawn() {
        return this.shouldDespawn;
    }

    /**
     * Sets whether this item should despawn when its age limit is reached.
     *
     * @param shouldDespawn whether the item should despawn
     */
    public void setShouldDespawn(boolean shouldDespawn) {
        this.shouldDespawn = shouldDespawn;
    }

    /**
     * Returns the persistent ActorUniqueID of this item's owner.
     *
     * @return owner ActorUniqueID
     */
    public long getOwnerId() {
        return this.ownerId;
    }

    /**
     * Sets the persistent ActorUniqueID of this item's owner.
     *
     * @param ownerId owner ActorUniqueID
     */
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        this.setDataProperty(ActorDataTypes.OWNER, ownerId);
    }

    @Override
    public String getOwnerName() {
        return this.ownerName;
    }

    /**
     * Sets the PNX owner name associated with this item entity.
     *
     * @param ownerName owner name
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @deprecated Use {@link #setOwnerName(String)}.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setOwner(String owner) {
        this.setOwnerName(owner);
    }

    /**
     * @deprecated Use {@link #getOwnerName()}.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
    public String getThrower() {
        return this.getOwnerName();
    }

    /**
     * @deprecated Use {@link #setOwnerName(String)}.
     */
    @Deprecated(since = "3.1.0", forRemoval = true)
    public void setThrower(String thrower) {
        this.setOwnerName(thrower);
    }

    private boolean canMergeItems() {
        return this.mergeItems &&
                this.shouldDespawn &&
                !this.isDisplayOnly;
    }

    @Override
    public BedrockPacket createAddEntityPacket() {
        final AddItemActorPacket addItemActorPacket = new AddItemActorPacket();
        addItemActorPacket.setEntityData(this.actorDataMap);
        addItemActorPacket.setTargetActorID(this.uniqueIdLong());
        addItemActorPacket.setTargetRuntimeID(this.runtimeId());
        addItemActorPacket.setItem(this.getItem().toNetwork());
        addItemActorPacket.setPosition(Vector3f.from(this.x, this.y + this.getBaseOffset(), this.z));
        addItemActorPacket.setVelocity(Vector3f.from(this.motionX, this.motionY, this.motionZ));
        return addItemActorPacket;
    }

    @Override
    public boolean doesTriggerPressurePlate() {
        return true;
    }
}
