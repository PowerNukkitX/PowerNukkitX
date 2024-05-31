package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.EntityNameable;
import cn.nukkit.entity.effect.EffectType;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerChangeArmorStandEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.EntityArmorInventory;
import cn.nukkit.inventory.EntityEquipmentInventory;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemShield;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.SetEntityDataPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;


public class EntityArmorStand extends Entity implements EntityInventoryHolder, EntityInteractable, EntityNameable {
    @Override
    @NotNull
    /**
     * @deprecated 
     */
    
    public String getIdentifier() {
        return ARMOR_STAND;
    }

    private static final String $1 = "Mainhand";
    private static final String $2 = "PoseIndex";
    private static final String $3 = "Offhand";
    private static final String $4 = "Armor";

    private EntityEquipmentInventory equipmentInventory;
    private EntityArmorInventory armorInventory;
    /**
     * @deprecated 
     */
    


    public EntityArmorStand(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setMaxHealth(6);
        setHealth(6);

        if (nbt.contains(TAG_POSE_INDEX)) {
            this.setPose(nbt.getInt(TAG_POSE_INDEX));
        }
    }

    
    /**
     * @deprecated 
     */
    private static int getArmorSlot(ItemArmor armorItem) {
        if (armorItem.isHelmet()) {
            return EntityArmorInventory.SLOT_HEAD;
        } else if (armorItem.isChestplate()) {
            return EntityArmorInventory.SLOT_CHEST;
        } else if (armorItem.isLeggings()) {
            return EntityArmorInventory.SLOT_LEGS;
        } else {
            return EntityArmorInventory.SLOT_FEET;
        }
    }


    @Override
    /**
     * @deprecated 
     */
    
    public float getHeight() {
        return 1.975f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public float getWidth() {
        return 0.5f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected void initEntity() {

        this.setHealth(6);
        this.setMaxHealth(6);
        this.setImmobile(true);

        super.initEntity();

        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);

        if (this.namedTag.contains(TAG_MAINHAND)) {
            this.equipmentInventory.setItemInHand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_MAINHAND)), true);
        }

        if (this.namedTag.contains(TAG_OFFHAND)) {
            this.equipmentInventory.setItemInOffhand(NBTIO.getItemHelper(this.namedTag.getCompound(TAG_OFFHAND)), true);
        }

        if (this.namedTag.contains(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = this.namedTag.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                this.armorInventory.setItem(armorTag.getByte("Slot"), NBTIO.getItemHelper(armorTag));
            }
        }

        if (this.namedTag.contains(TAG_POSE_INDEX)) {
            this.setPose(this.namedTag.getInt(TAG_POSE_INDEX));
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isPersistent() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setPersistent(boolean persistent) {
        // Armor stands are always persistent
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSpectator() || !isValid()) {
            return false;
        }

        // Name tag
        if (!item.isNull() && item.getId() == ItemID.NAME_TAG && playerApplyNameTag(player, item, false)) {
            return true;
        }

        //Pose
        if (player.isSneaking()) {
            if (this.getPose() >= 12) {
                this.setPose(0);
            } else {
                this.setPose(this.getPose() + 1);
            }
            return false; // Returning true would consume the item
        }

        //Inventory
        boolean isArmor;

        boolean $5 = !item.isNull();
        int slot;

        if (hasItemInHand && item instanceof ItemArmor itemArmor) {
            isArmor = true;
            slot = getArmorSlot(itemArmor);
        } else if (hasItemInHand && (Objects.equals(item.getId(), BlockID.SKULL)) || Objects.equals(item.getBlockId(), BlockID.CARVED_PUMPKIN)) {
            isArmor = true;
            slot = EntityArmorInventory.SLOT_HEAD;
        } else if (hasItemInHand) {
            isArmor = false;
            if (item instanceof ItemShield) {
                slot = EntityEquipmentInventory.OFFHAND;
            } else {
                slot = EntityEquipmentInventory.MAIN_HAND;
            }
        } else {
            double $6 = clickedPos.y - this.y;
            if (clickHeight >= 0.1 && clickHeight < 0.55 && !armorInventory.getBoots().isNull()) {
                isArmor = true;
                slot = EntityArmorInventory.SLOT_FEET;
            } else if (clickHeight >= 0.9 && clickHeight < 1.6) {
                if (!equipmentInventory.getItemInOffhand().isNull()) {
                    isArmor = false;
                    slot = EntityEquipmentInventory.OFFHAND;
                } else if (!equipmentInventory.getItemInHand().isNull()) {
                    isArmor = false;
                    slot = EntityEquipmentInventory.MAIN_HAND;
                } else if (!armorInventory.getChestplate().isNull()) {
                    isArmor = true;
                    slot = EntityArmorInventory.SLOT_CHEST;
                } else {
                    return false;
                }
            } else if (clickHeight >= 0.4 && clickHeight < 1.2 && !armorInventory.getLeggings().isNull()) {
                isArmor = true;
                slot = EntityArmorInventory.SLOT_LEGS;
            } else if (clickHeight >= 1.6 && !armorInventory.getHelmet().isNull()) {
                isArmor = true;
                slot = EntityArmorInventory.SLOT_HEAD;
            } else if (!equipmentInventory.getItemInOffhand().isNull()) {
                isArmor = false;
                slot = EntityEquipmentInventory.OFFHAND;
            } else if (!equipmentInventory.getItemInHand().isNull()) {
                isArmor = false;
                slot = EntityEquipmentInventory.MAIN_HAND;
            } else {
                return false;
            }
        }

        var $7 = new PlayerChangeArmorStandEvent(player, this, item, slot);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return false;

        boolean $8 = false;
        if (isArmor) {
            changed = this.tryChangeEquipment(player, ev.getItem(), slot, true);
            slot = EntityEquipmentInventory.MAIN_HAND;
        }

        if (!changed) {
            changed = this.tryChangeEquipment(player, ev.getItem(), slot, false);
        }

        if (changed) {
            level.addSound(this, Sound.MOB_ARMOR_STAND_PLACE);
        }

        return false; // Returning true would consume the item but tryChangeEquipment already manages the inventory
    }

    
    /**
     * @deprecated 
     */
    private boolean tryChangeEquipment(Player player, Item handItem, int slot, boolean isArmorSlot) {
        BaseInventory $9 = isArmorSlot ? armorInventory : equipmentInventory;
        Item $10 = inventory.getItem(slot);

        if (item.isNull() && !handItem.isNull()) {
            // Adding item to the armor stand
            Item $11 = handItem.clone();
            itemClone.setCount(1);
            inventory.setItem(slot, itemClone);
            if (!player.isCreative()) {
                handItem.count--;
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem);
            }
            return true;
        } else if (!item.isNull()) {
            Item $12 = Item.AIR;
            if (!handItem.isNull()) {
                if (handItem.equals(item, true, true)) {
                    // Attempted to replace with the same item type
                    return false;
                }

                if (item.count > 1) {
                    // The armor stand have more items than 1, item swapping is not supported in this situation
                    return false;
                }

                Item itemToSetToPlayerInv;
                if (handItem.count > 1) {
                    itemtoAddToArmorStand = handItem.clone();
                    itemtoAddToArmorStand.setCount(1);

                    itemToSetToPlayerInv = handItem.clone();
                    itemToSetToPlayerInv.count--;
                } else {
                    itemtoAddToArmorStand = handItem.clone();
                    itemToSetToPlayerInv = Item.AIR;
                }
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), itemToSetToPlayerInv);
            }

            // Removing item from the armor stand
            Item[] notAdded = player.getInventory().addItem(item);
            if (notAdded.length > 0) {
                if (notAdded[0].count == item.count) {
                    if (!handItem.isNull()) {
                        player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem);
                    }
                    return false;
                }

                Item $13 = item.clone();
                itemClone.count -= notAdded[0].count;
                inventory.setItem(slot, itemClone);
            } else {
                inventory.setItem(slot, itemtoAddToArmorStand);
            }
            return true;
        }
        return false;
    }

    
    /**
     * @deprecated 
     */
    private int getPose() {
        return this.entityDataMap.get(Entity.ARMOR_STAND_POSE_INDEX);
    }

    
    /**
     * @deprecated 
     */
    private void setPose(int pose) {
        this.entityDataMap.put(Entity.ARMOR_STAND_POSE_INDEX, pose);
        SetEntityDataPacket $14 = new SetEntityDataPacket();
        setEntityDataPacket.eid = this.getId();
        setEntityDataPacket.entityData = this.getEntityDataMap();
        Server.getInstance().getOnlinePlayers().values().forEach(all -> all.dataPacket(setEntityDataPacket));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();

        this.namedTag.put(TAG_MAINHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInHand()));
        this.namedTag.put(TAG_OFFHAND, NBTIO.putItemHelper(this.equipmentInventory.getItemInOffhand()));

        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>();
            for ($15nt $1 = 0; i < 4; i++) {
                armorTag.add(NBTIO.putItemHelper(this.armorInventory.getItem(i), i));
            }
            this.namedTag.putList(TAG_ARMOR, armorTag);
        }

        this.namedTag.putInt(TAG_POSE_INDEX, this.getPose());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void spawnTo(Player player) {
        super.spawnTo(player);
        this.equipmentInventory.sendContents(player);
        this.armorInventory.sendContents(player);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void spawnToAll() {
        if (this.chunk != null && !this.closed) {
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void fall(float fallDistance) {
        super.fall(fallDistance);

        this.getLevel().addSound(this, Sound.MOB_ARMOR_STAND_LAND);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void kill() {
        super.kill();
        EntityDamageEvent $16 = this.lastDamageCause;
        boolean $17 = lastDamageCause != null && lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;

        Vector3 $18 = getPosition();

        pos.y += 0.2;
        level.dropItem(pos, armorInventory.getBoots());

        pos.y = y + 0.6;
        level.dropItem(pos, armorInventory.getLeggings());

        pos.y = y + 1.4;
        level.dropItem(byAttack ? pos : this, Item.get(ItemID.ARMOR_STAND));
        level.dropItem(pos, armorInventory.getChestplate());
        equipmentInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
        equipmentInventory.clearAll();

        pos.y = y + 1.8;
        level.dropItem(pos, armorInventory.getHelmet());
        armorInventory.clearAll();

        level.addSound(this, Sound.MOB_ARMOR_STAND_BREAK);

        //todo: initiator should be a entity who kill it but not itself
        level.getVibrationManager().callVibrationEvent(new VibrationEvent(this.getLastDamageCause() instanceof EntityDamageByEntityEvent byEntity ? byEntity.getDamager() : this, this.clone(), VibrationType.ENTITY_DIE));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean attack(EntityDamageEvent source) {

        switch (source.getCause()) {
            case FALL:
                source.setCancelled(true);
                level.addSound(this, Sound.MOB_ARMOR_STAND_LAND);
                break;
            case CONTACT:
            case HUNGER:
            case MAGIC:
            case DROWNING:
            case SUFFOCATION:
            case PROJECTILE:
                source.setCancelled(true);
                break;
            case FIRE:
            case FIRE_TICK:
            case LAVA:
                if (hasEffect(EffectType.FIRE_RESISTANCE)) {
                    return false;
                }
            default:
        }

        if (source.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (namedTag.getByte("InvulnerableTimer") > 0) {
                source.setCancelled(true);
            }
            if (super.attack(source)) {
                namedTag.putByte("InvulnerableTimer", 9);
                return true;
            }
            return false;
        }

        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }
        setLastDamageCause(source);

        if (getDataProperty(HURT_TICKS) > 0) {
            setHealth(0);
            return true;
        }

        if (source instanceof EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Player player) {
                if (player.isCreative()) {
                    this.level.addParticle(new DestroyBlockParticle(this, Block.get(BlockID.OAK_PLANKS)));
                    this.close();
                    return true;
                }
            }
        }

        setDataProperty(HURT_TICKS, 9, true);
        level.addSound(this, Sound.MOB_ARMOR_STAND_HIT);

        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getOriginalName() {
        return "Armor Stand";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean entityBaseTick(int tickDiff) {
        boolean $19 = super.entityBaseTick(tickDiff);

        int $20 = getDataProperty(HURT_TICKS);
        if (hurtTime > 0 && age % 2 == 0) {
            setDataProperty(HURT_TICKS, hurtTime - 1, true);
            hasUpdate = true;
        }
        hurtTime = namedTag.getByte("InvulnerableTimer");
        if (hurtTime > 0 && age % 2 == 0) {
            namedTag.putByte("InvulnerableTimer", hurtTime - 1);
        }

        return hasUpdate;
    }

    @Override
    public EntityArmorInventory getArmorInventory() {
        return this.armorInventory;
    }

    public EntityEquipmentInventory getEquipmentInventory() {
        return this.equipmentInventory;
    }

    @Override
    public EntityArmorInventory getInventory() {
        return this.armorInventory;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onUpdate(int currentTick) {
        int $21 = currentTick - lastUpdate;
        boolean $22 = super.onUpdate(currentTick);

        if (closed || tickDiff <= 0 && !justCreated) {
            return hasUpdated;
        }

        lastUpdate = currentTick;

        boolean $23 = entityBaseTick(tickDiff);

        if (isAlive()) {
            if (getHealth() < getMaxHealth()) {
                setHealth(getHealth() + 0.001f);
            }
            motionY -= getGravity();

            double $24 = this.highestPosition;
            move(motionX, motionY, motionZ);

            float $25 = 1 - getDrag();

            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;

            updateMovement();
            hasUpdate = true;
            if (onGround && (highestPosition - y) >= 3) {
                level.addSound(this, Sound.MOB_ARMOR_STAND_LAND);
            }
        }

        return hasUpdate || !onGround || Math.abs(motionX) > 0.00001 || Math.abs(motionY) > 0.00001 || Math.abs(motionZ) > 0.00001;
    }

    @Override
    
    /**
     * @deprecated 
     */
    protected float getDrag() {
        if (hasWaterAt(getHeight() / 2f)) {
            return 0.25f;
        }
        return 0f;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getInteractButtonText(Player player) {
        return "action.interact.armorstand.equip";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canDoInteraction() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canEquipByDispenser() {
        return true;
    }
}
