package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.HumanEnderChestInventory;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.HumanOffHandInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemShield;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.network.protocol.types.EntityLink;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用来提供给插件基础，以方便的使用带有智能的EntityHuman
 */


public class EntityIntelligentHuman extends EntityIntelligent implements EntityInventoryHolder, IHuman {
    @Override
    public @NotNull String getIdentifier() {
        return PLAYER;
    }

    protected UUID uuid;
    protected byte[] rawUUID;
    protected Skin skin;
    protected HumanInventory inventory;
    protected HumanEnderChestInventory enderChestInventory;
    protected HumanOffHandInventory offhandInventory;

    public EntityIntelligentHuman(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public float getSwimmingHeight() {
        return getWidth();
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
    public float getEyeHeight() {
        return (float) (boundingBox.getMaxY() - boundingBox.getMinY() - 0.18);
    }

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

    public byte[] getRawUniqueId() {
        return rawUUID;
    }

    @Override
    public HumanInventory getInventory() {
        return inventory;
    }

    public HumanEnderChestInventory getEnderChestInventory() {
        return enderChestInventory;
    }

    public HumanOffHandInventory getOffhandInventory() {
        return offhandInventory;
    }

    @Override
    public void setInventories(Inventory[] inventory) {
        this.inventory = (HumanInventory) inventory[0];
        this.offhandInventory = (HumanOffHandInventory) inventory[1];
        this.enderChestInventory = (HumanEnderChestInventory) inventory[2];
    }

    @Override
    protected void initEntity() {
        initHumanEntity(this);
        super.initEntity();
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        saveHumanEntity(this);
    }

    @Override
    public void updateMovement() {
        // 检测自由落体时间
        if (!this.onGround && this.y < this.highestPosition) {
            this.fallingTick++;
        }
        //这样做是为了向后兼容旧插件
        if (!enableHeadYaw()) {
            this.headYaw = this.yaw;
        }
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = enableHeadYaw() ? (this.headYaw - this.lastHeadYaw) * (this.headYaw - this.lastHeadYaw) : 0 + (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);
        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);
        if (diffPosition > 0.0001 || diffRotation > 1.0) { //0.2 ** 2, 1.5 ** 2
            if (diffPosition > 0.0001) {
                if (this.isOnGround()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.STEP));
                } else if (this.isTouchingWater()) {
                    this.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, this.clone(), VibrationType.SWIM));
                }
            }
            this.broadcastMovement(false);
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;
            this.lastPitch = this.pitch;
            this.lastYaw = this.yaw;
            this.lastHeadYaw = this.headYaw;
            this.positionChanged = true;
        } else {
            this.positionChanged = false;
        }
        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) { //0.05 ** 2
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;
            this.addMotion(this.motionX, this.motionY, this.motionZ);
        }
        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public Item[] getDrops() {
        if (this.inventory != null) {
            List<Item> drops = new ArrayList<>(this.inventory.getContents().values());
            drops.addAll(this.offhandInventory.getContents().values());
            return drops.stream().filter(item -> !item.keepOnDeath()).toList().toArray(Item.EMPTY_ARRAY);
        }
        return Item.EMPTY_ARRAY;
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isClosed() || !this.isAlive()) {
            return false;
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.VOID && source.getCause() != EntityDamageEvent.DamageCause.CUSTOM && source.getCause() != EntityDamageEvent.DamageCause.MAGIC && source.getCause() != EntityDamageEvent.DamageCause.HUNGER) {
            int armorPoints = 0;
            int epf = 0;
            //int toughness = 0;

            for (Item armor : inventory.getArmorContents()) {
                armorPoints += armor.getArmorPoints();
                epf += calculateEnchantmentProtectionFactor(armor, source);
                //toughness += armor.getToughness();
            }

            if (source.canBeReducedByArmor()) {
                source.setDamage(-source.getFinalDamage() * armorPoints * 0.04f, EntityDamageEvent.DamageModifier.ARMOR);
            }

            source.setDamage(-source.getFinalDamage() * Math.min(NukkitMath.ceilFloat(Math.min(epf, 25) * ((float) ThreadLocalRandom.current().nextInt(50, 100) / 100)), 20) * 0.04f,
                    EntityDamageEvent.DamageModifier.ARMOR_ENCHANTMENTS);

            source.setDamage(-Math.min(this.getAbsorption(), source.getFinalDamage()), EntityDamageEvent.DamageModifier.ABSORPTION);
        }

        if (super.attack(source)) {
            Entity damager = null;

            if (source instanceof EntityDamageByEntityEvent) {
                damager = ((EntityDamageByEntityEvent) source).getDamager();
            }
            for (int slot = 0; slot < 4; slot++) {
                Item armorOld = this.inventory.getArmorItem(slot);
                if (armorOld.isArmor()) {
                    Item armor = damageArmor(armorOld, damager, source);
                    inventory.setArmorItem(slot, armor, armor.getId() != BlockID.AIR);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    protected double calculateEnchantmentProtectionFactor(Item item, EntityDamageEvent source) {
        if (!item.hasEnchantments()) {
            return 0;
        }
        double epf = 0;
        if (item.applyEnchantments()) {
            for (Enchantment ench : item.getEnchantments()) {
                epf += ench.getProtectionFactor(source);
            }
        }
        return epf;
    }

    @Override
    public void setOnFire(int seconds) {
        int level = 0;
        for (Item armor : this.inventory.getArmorContents()) {
            Enchantment fireProtection = armor.getEnchantment(Enchantment.ID_PROTECTION_FIRE);

            if (fireProtection != null && fireProtection.getLevel() > 0) {
                level = Math.max(level, fireProtection.getLevel());
            }
        }
        seconds = (int) (seconds * (1 - level * 0.15));
        super.setOnFire(seconds);
    }

    @Override
    protected boolean applyNameTag(@NotNull Player player, @NotNull Item item) {
        return false;
    }

    protected Item damageArmor(Item armor, Entity damager, EntityDamageEvent event) {
        if (armor.hasEnchantments()) {
            if (damager != null) {
                if (armor.applyEnchantments()) {
                    for (Enchantment enchantment : armor.getEnchantments()) {
                        enchantment.doPostAttack(damager, this);
                    }
                }
            }

            Enchantment durability = armor.getEnchantment(Enchantment.ID_DURABILITY);
            if (durability != null
                    && durability.getLevel() > 0
                    && (100 / (durability.getLevel() + 1)) <= Utils.random.nextInt(100)) {
                return armor;
            }
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.VOID &&
                event.getCause() != EntityDamageEvent.DamageCause.MAGIC &&
                event.getCause() != EntityDamageEvent.DamageCause.HUNGER &&
                event.getCause() != EntityDamageEvent.DamageCause.DROWNING &&
                event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION &&
                event.getCause() != EntityDamageEvent.DamageCause.SUICIDE &&
                event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK &&
                event.getCause() != EntityDamageEvent.DamageCause.FALL) { // No armor damage

            if (armor.isUnbreakable() || armor.getMaxDurability() < 0) {
                return armor;
            }

            if (armor instanceof ItemShield)
                armor.setDamage(armor.getDamage() + (event.getDamage() >= 3 ? (int) event.getDamage() + 1 : 0));
            else
                armor.setDamage(armor.getDamage() + Math.max(1, (int) (event.getDamage() / 4.0f)));

            if (armor.getDamage() >= armor.getMaxDurability()) {
                getLevel().addSound(this, Sound.RANDOM_BREAK);
                return Item.get(BlockID.AIR, 0, 0);
            }
        }

        return armor;
    }

    @Override
    public String getOriginalName() {
        return "EntityIntelligentHuman";
    }

    @Override
    @NotNull
    public String getName() {
        return this.getNameTag();
    }

    @Override
    public void addMovement(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.level.addPlayerMovement(this, x, y, z, yaw, pitch, headYaw);
    }

    @Override
    public void spawnTo(Player player) {
        if (!this.hasSpawned.containsKey(player.getLoaderId())) {
            this.hasSpawned.put(player.getLoaderId(), player);

            if (!this.skin.isValid()) {
                throw new IllegalStateException(this.getClass().getSimpleName() + " must have a valid skin set");
            }

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
            this.server.removePlayerListData(this.getUniqueId(), player);
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
            if (inventory != null) {
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
    public EntityArmorInventory getArmorInventory() {
        return null;
    }

    @Override
    public EntityEquipmentInventory getEquipmentInventory() {
        return null;
    }

    public Item getHelmet() {
        return this.getInventory().getHelmet();
    }

    public boolean setHelmet(Item item) {
        return this.getInventory().setHelmet(item);
    }

    public Item getChestplate() {
        return this.getInventory().getChestplate();
    }

    public boolean setChestplate(Item item) {
        return this.getInventory().setChestplate(item);
    }

    public Item getLeggings() {
        return getInventory().getLeggings();
    }

    public boolean setLeggings(Item item) {
        return getInventory().setLeggings(item);
    }

    public Item getBoots() {
        return getInventory().getBoots();
    }

    public boolean setBoots(Item item) {
        return getInventory().setBoots(item);
    }

    public Item getItemInHand() {
        return getInventory().getItemInHand();
    }

    public Item getItemInOffhand() {
        return this.getOffhandInventory().getItem(0);
    }

    public boolean setItemInHand(Item item) {
        return getInventory().setItemInHand(item);
    }

    public boolean setItemInHand(Item item, boolean send) {
        return this.getInventory().setItem(getInventory().getHeldItemIndex(), item, send);
    }

    public boolean setItemInOffhand(Item item) {
        return this.getOffhandInventory().setItem(0, item, true);
    }

    public boolean setItemInOffhand(Item item, boolean send) {
        return this.getOffhandInventory().setItem(0, item, send);
    }
}
