package org.powernukkitx.entity.projectile;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.effect.PotionApplicationMode;
import org.powernukkitx.item.ItemArrow;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorEvent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.packet.ActorEventPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class EntityArrow extends SlenderProjectile {

    @Override
    @NotNull
    public String getIdentifier() {
        return ARROW;
    }

    private static final int VANILLA_GROUNDED_LIFETIME = 1200;
    private static final String PNX_PICKUP_MODE = "PickupMode";

    protected int pickupMode;
    protected long ownerId;
    protected boolean playerOwned;
    protected boolean creativeOrigin;
    protected boolean hasPickupModeOverride;
    protected ItemArrow item;
    private int life;

    public EntityArrow(IChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityArrow(IChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);

        if (shootingEntity != null) {
            this.setOwnerId(shootingEntity.uniqueIdLong());
            this.playerOwned = shootingEntity instanceof Player;
            this.creativeOrigin = shootingEntity instanceof Player player && player.isCreative();
        }

        this.setCritical(critical);
    }


    @Override
    public float getLength() {
        return 0.5f;
    }

    @Override
    public float getDefaultGravity() {
        return 0.05f;
    }

    @Override
    public float getDrag() {
        return 0.01f;
    }

    @Override
    protected void updateMotion() {
        if (!isInsideOfWater()) {
            super.updateMotion();
            return;
        }

        float drag = 1 - this.getDrag() * 20;

        motionY -= getGravity() * 2;
        if (motionY < 0) {
            motionY *= drag / 1.5;
        }
        motionX *= drag;
        motionZ *= drag;
    }

    @Override
    protected void initEntity() {
        this.setHasAge(false);
        super.initEntity();

        final CompoundTag nbtMap = this.getNbt();

        this.life = 0;
        this.ownerId = nbtMap.getLong("OwnerID");
        this.playerOwned = nbtMap.getBoolean("player");
        this.creativeOrigin = nbtMap.getBoolean("isCreative");
        this.pickupMode = PICKUP_ANY;
        this.hasPickupModeOverride = false;

        CompoundTag customData = nbtMap.containsCompound(NBT_PNX_CUSTOM)
                ? nbtMap.getCompound(NBT_PNX_CUSTOM)
                : new CompoundTag();

        if (customData.containsNumber(PNX_PICKUP_MODE)) {
            this.pickupMode = customData.getInt(PNX_PICKUP_MODE);
            this.hasPickupModeOverride = true;
        }

        this.readStoredEnchantments(nbtMap);
        this.setItem(new ItemArrow(Byte.toUnsignedInt(nbtMap.getByte("auxValue")), 1));
        this.actorDataMap.put(ActorDataTypes.OWNER, this.ownerId);
    }

    public void setCritical() {
        this.setCritical(true);
    }

    public boolean isCritical() {
        return this.getDataFlag(ActorFlags.CRITICAL);
    }

    public void setCritical(boolean value) {
        this.setDataFlag(ActorFlags.CRITICAL, value);
    }

    @Override
    public int getResultDamage() {
        int base = super.getResultDamage();

        if (this.isCritical()) {
            base += ThreadLocalRandom.current().nextInt(base / 2 + 2);
        }

        return base;
    }

    @Override
    protected double getBaseDamage() {
        return 2;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }

        int tickDiff = currentTick - this.lastUpdate;
        boolean hasUpdate = super.onUpdate(currentTick);

        if (this.closed) {
            return true;
        }

        if (this.onGround || this.hadCollision) {
            this.setCritical(false);

            if (tickDiff > 0) {
                this.life += tickDiff;
            }

            if (this.life >= VANILLA_GROUNDED_LIFETIME) {
                this.close();
                hasUpdate = true;
            }
        }

        if (this.level.isRaining() && this.fireTicks > 0 && this.level.canBlockSeeSky(this)) {
            extinguish();

            hasUpdate = true;
        }

        return hasUpdate;
    }

    @Override
    public boolean canBeMovedByCurrents() {
        return !hadCollision;
    }

    @Override
    protected void afterCollisionWithEntity(Entity entity) {
        if (hadCollision) {
            if (getArrowItem() != null) {
                if (getArrowItem().getTippedArrowPotion() != null) {
                    getArrowItem().getTippedArrowPotion().getEffects(PotionApplicationMode.ARROW).forEach(entity::addEffect);
                }
            }
            close();
        } else {
            setMotion(getMotion().divide(-4));
        }
    }

    @Override
    protected void addHitEffect() {
        this.level.addSound(this, Sound.RANDOM_BOWHIT);
        final ActorEventPacket packet = new ActorEventPacket();
        packet.setTargetRuntimeID(this.runtimeId());
        packet.setType(ActorEvent.SHAKE);
        packet.setData(7); // TODO Magic value. I have no idea why we have to set it to 7 here...
        Server.broadcastPacket(this.hasSpawned.values(), packet);
        onGround = true;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        this.nbt.putLong("OwnerID", this.ownerId)
                .putByte("player", this.playerOwned ? 1 : 0)
                .putByte("isCreative", this.creativeOrigin ? 1 : 0)
                .putByte("enchantPower", this.getStoredEnchantmentLevel(Enchantment.ID_BOW_POWER))
                .putByte("enchantPunch", this.getStoredEnchantmentLevel(Enchantment.ID_BOW_KNOCKBACK))
                .putByte("enchantFlame", this.getStoredEnchantmentLevel(Enchantment.ID_BOW_FLAME))
                .putByte("enchantInfinity", this.getStoredEnchantmentLevel(Enchantment.ID_BOW_INFINITY))
                .putByte("auxValue", this.item != null ? this.item.getDamage() : 0);

        CompoundTag customData = this.nbt.containsCompound(NBT_PNX_CUSTOM)
                ? this.nbt.getCompound(NBT_PNX_CUSTOM).copy()
                : new CompoundTag();

        if (this.hasPickupModeOverride) {
            customData.putInt(PNX_PICKUP_MODE, this.pickupMode);
        } else {
            customData.remove(PNX_PICKUP_MODE);
        }

        if (customData.isEmpty()) {
            this.nbt.remove(NBT_PNX_CUSTOM);
        } else {
            this.nbt.putCompound(NBT_PNX_CUSTOM, customData);
        }
    }

    public int getPickupMode() {
        if (this.hasPickupModeOverride) {
            return this.pickupMode;
        }

        return this.playerOwned
                ? PICKUP_ANY
                : PICKUP_NONE;
    }

    public void setPickupMode(int pickupMode) {
        this.pickupMode = pickupMode;
        this.hasPickupModeOverride = true;
    }

    /**
     * Returns the persistent ActorUniqueID of this arrow's owner.
     *
     * @return owner ActorUniqueID
     */
    public long getOwnerId() {
        return this.ownerId;
    }

    /**
     * Sets the persistent ActorUniqueID of this arrow's owner.
     *
     * @param ownerId owner ActorUniqueID
     */
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
        this.setDataProperty(ActorDataTypes.OWNER, ownerId);
    }

    /**
     * Returns whether this arrow was fired by a player.
     *
     * @return whether the arrow is player-owned
     */
    public boolean isPlayerOwned() {
        return this.playerOwned;
    }

    /**
     * Sets whether this arrow was fired by a player.
     *
     * @param playerOwned whether the arrow is player-owned
     */
    public void setPlayerOwned(boolean playerOwned) {
        this.playerOwned = playerOwned;
    }

    /**
     * Returns whether this arrow originated from a creative-mode shot.
     *
     * @return whether the arrow has creative origin
     */
    public boolean isCreativeOrigin() {
        return this.creativeOrigin;
    }

    /**
     * Sets whether this arrow originated from a creative-mode shot.
     *
     * @param creativeOrigin whether the arrow has creative origin
     */
    public void setCreativeOrigin(boolean creativeOrigin) {
        this.creativeOrigin = creativeOrigin;
    }

    /**
     * Returns whether the specified player may pick up this arrow.
     *
     * @param player player attempting pickup
     * @return whether pickup is allowed
     */
    public boolean canBePickedUpBy(Player player) {
        if (this.hasPickupModeOverride) {
            return this.pickupMode != PICKUP_NONE &&
                    (this.pickupMode != PICKUP_CREATIVE || player.isCreative());
        }

        return this.playerOwned;
    }

    /**
     * Returns whether picking up this arrow should return an arrow item.
     *
     * @return whether an item should be returned
     */
    public boolean shouldReturnItemOnPickup() {
        return !this.creativeOrigin &&
                this.getStoredEnchantmentLevel(Enchantment.ID_BOW_INFINITY) == 0;
    }

    private void readStoredEnchantments(CompoundTag nbtMap) {
        List<Enchantment> storedEnchantments = new ArrayList<>(4);

        int power = Byte.toUnsignedInt(nbtMap.getByte("enchantPower"));
        if (power > 0) {
            storedEnchantments.add(
                    Enchantment.getEnchantment(Enchantment.ID_BOW_POWER)
                            .setLevel(power, false)
            );
        }

        int punch = Byte.toUnsignedInt(nbtMap.getByte("enchantPunch"));
        if (punch > 0) {
            storedEnchantments.add(
                    Enchantment.getEnchantment(Enchantment.ID_BOW_KNOCKBACK)
                            .setLevel(punch, false)
            );
        }

        int flame = Byte.toUnsignedInt(nbtMap.getByte("enchantFlame"));
        if (flame > 0) {
            storedEnchantments.add(
                    Enchantment.getEnchantment(Enchantment.ID_BOW_FLAME)
                            .setLevel(flame, false)
            );
        }

        int infinity = Byte.toUnsignedInt(nbtMap.getByte("enchantInfinity"));
        if (infinity > 0) {
            storedEnchantments.add(
                    Enchantment.getEnchantment(Enchantment.ID_BOW_INFINITY)
                            .setLevel(infinity, false)
            );
        }

        this.enchantments = storedEnchantments.size() == 0
                ? null
                : storedEnchantments.toArray(Enchantment[]::new);
    }

    private int getStoredEnchantmentLevel(int id) {
        if (this.enchantments == null) {
            return 0;
        }

        for (Enchantment enchantment : this.enchantments) {
            if (enchantment.getId() == id) {
                return enchantment.getLevel();
            }
        }

        return 0;
    }

    public void setItem(ItemArrow arrow) {
        this.item = arrow;
        if (arrow.getTippedArrowPotion() != null) {
            this.setDataProperty(ActorDataTypes.CUSTOM_DISPLAY, (byte) (arrow.getTippedArrowPotion().id() + 1));
        }
    }

    public ItemArrow getArrowItem() {
        return this.item;
    }

    @Override
    public String getOriginalName() {
        return "Arrow";
    }
}
