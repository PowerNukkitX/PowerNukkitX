package org.powernukkitx.entity.item;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityInteractable;
import org.powernukkitx.entity.components.NameableComponent;
import org.powernukkitx.entity.effect.EffectType;
import org.powernukkitx.event.entity.EntityDamageByEntityEvent;
import org.powernukkitx.event.entity.EntityDamageEvent;
import org.powernukkitx.event.player.PlayerChangeArmorStandEvent;
import org.powernukkitx.inventory.BaseInventory;
import org.powernukkitx.inventory.EntityArmorInventory;
import org.powernukkitx.inventory.EntityEquipmentInventory;
import org.powernukkitx.inventory.EntityInventoryHolder;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.level.Sound;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.DestroyBlockParticle;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;

import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.SetActorDataPacket;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class EntityArmorStand extends Entity implements EntityInventoryHolder, EntityInteractable {
    @Override
    @NotNull
    public String getIdentifier() {
        return ARMOR_STAND;
    }

    private static final String TAG_MAINHAND = "Mainhand";
    private static final String TAG_POSE_INDEX = "PoseIndex";
    private static final String TAG_OFFHAND = "Offhand";
    private static final String TAG_ARMOR = "Armor";

    private EntityEquipmentInventory equipmentInventory;
    private EntityArmorInventory armorInventory;


    public EntityArmorStand(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        setHealthMax(6);
        setHealthCurrent(6);

        if (nbt.contains(TAG_POSE_INDEX)) {
            this.setPose(nbt.getInt(TAG_POSE_INDEX));
        }
    }

    private static int getArmorSlot(Item armorItem) {
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
    public float getHeight() {
        return 1.975f;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    protected float getDefaultGravity() {
        return 0.04f;
    }

    @Override
    protected boolean shouldStopMotionWhenImmobile() {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    protected void initEntity() {
        this.setHealthCurrent(6);
        this.setHealthMax(6);
        this.setImmobile(true);

        super.initEntity();

        this.equipmentInventory = new EntityEquipmentInventory(this);
        this.armorInventory = new EntityArmorInventory(this);
        this.actorDataMap.putIfAbsent(ActorDataTypes.HURT, 0);
        this.actorDataMap.putIfAbsent(ActorDataTypes.POSE_INDEX, 0);

        final CompoundTag nbtMap = this.getNbt();
        if (nbtMap.contains(TAG_MAINHAND)) {
            final Item mainhand = ItemHelper.read(nbtMap.getCompound(TAG_MAINHAND));
            this.equipmentInventory.setItemInHand(mainhand, true);
        }

        if (nbtMap.contains(TAG_OFFHAND)) {
            final Item offhand = ItemHelper.read(nbtMap.getCompound(TAG_OFFHAND));
            this.equipmentInventory.setItemInOffhand(offhand, true);
        }

        if (nbtMap.contains(TAG_ARMOR)) {
            ListTag<CompoundTag> armorList = nbtMap.getList(TAG_ARMOR, CompoundTag.class);
            for (CompoundTag armorTag : armorList.getAll()) {
                final int slot = armorTag.getByte("Slot");
                final Item armorItem = ItemHelper.read(armorTag);
                this.armorInventory.setItem(slot, armorItem);
            }
        }

        if (nbtMap.contains(TAG_POSE_INDEX)) {
            this.setPose(nbtMap.getInt(TAG_POSE_INDEX));
        }
    }

    protected boolean trySetNameTag(Player player, Item item) {
        NameableComponent nameable = getComponentNameable();
        if (nameable == null || nameable.isEmpty()) return false;

        if (!item.hasCustomName()) return false;
        if (!nameable.resolvedAllowNameTagRenaming()) return false;
        if (!player.isSneaking()) return false;

        this.setNameTag(item.getCustomName());
        this.setNameTagVisible(nameable.resolvedAlwaysShow());

        return true;
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (player.isSpectator() || !isValid()) {
            return false;
        }

        // Name tag
        if (!item.isNull() && item.getId().equals(Item.NAME_TAG) && isNameable() && trySetNameTag(player, item))
            return true;

        //Pose
        if (player.isSneaking()) {
            if (this.getPose() >= 12) {
                this.setPose(0);
            } else {
                this.setPose(this.getPose() + 1);
            }
            this.markChunkChanged();
            return false; // Returning true would consume the item
        }

        //Inventory
        boolean isArmor;

        boolean hasItemInHand = !item.isNull();
        int slot;

        if (hasItemInHand && item.isArmor()) {
            isArmor = true;
            slot = getArmorSlot(item);
        } else if (hasItemInHand && (Objects.equals(item.getId(), BlockID.SKULL)) || Objects.equals(item.getBlockId(), BlockID.CARVED_PUMPKIN)) {
            isArmor = true;
            slot = EntityArmorInventory.SLOT_HEAD;
        } else if (hasItemInHand) {
            isArmor = false;
            if (item.isShield()) {
                slot = EntityEquipmentInventory.OFFHAND;
            } else {
                slot = EntityEquipmentInventory.MAIN_HAND;
            }
        } else {
            double clickHeight = clickedPos.y - this.y;
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

        var ev = new PlayerChangeArmorStandEvent(player, this, item, slot);
        this.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) return false;

        boolean changed = false;
        if (isArmor) {
            changed = this.tryChangeEquipment(player, ev.getItem(), slot, true);
            slot = EntityEquipmentInventory.MAIN_HAND;
        }

        if (!changed) {
            changed = this.tryChangeEquipment(player, ev.getItem(), slot, false);
        }

        if (changed) {
            this.markChunkChanged();
            level.addSound(this, Sound.MOB_ARMOR_STAND_PLACE);
        }

        return false; // Returning true would consume the item but tryChangeEquipment already manages the inventory
    }

    private boolean tryChangeEquipment(Player player, Item handItem, int slot, boolean isArmorSlot) {
        BaseInventory inventory = isArmorSlot ? armorInventory : equipmentInventory;
        Item item = inventory.getItem(slot);

        if (item.isNull() && !handItem.isNull()) {
            // Adding item to the armor stand
            Item itemClone = handItem.clone();
            itemClone.setCount(1);
            inventory.setItem(slot, itemClone);
            if (!player.isCreative()) {
                handItem.count--;
                player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem);
            }
            return true;
        } else if (!item.isNull()) {
            Item itemtoAddToArmorStand = Item.AIR;
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

            Item[] notAdded = player.getInventory().addItem(item);
            if (notAdded.length > 0) {
                if (notAdded[0].count == item.count) {
                    if (!handItem.isNull()) {
                        player.getInventory().setItem(player.getInventory().getHeldItemIndex(), handItem);
                    }
                    return false;
                }

                Item itemClone = item.clone();
                itemClone.count -= notAdded[0].count;
                inventory.setItem(slot, itemClone);
            } else {
                inventory.setItem(slot, itemtoAddToArmorStand);
            }
            return true;
        }
        return false;
    }

    private int getPose() {
        Integer pose = this.actorDataMap.get(ActorDataTypes.POSE_INDEX);
        return pose == null ? 0 : pose;
    }

    private int getHurtTicks() {
        Integer hurt = this.getDataProperty(ActorDataTypes.HURT);
        return hurt == null ? 0 : hurt;
    }

    private void setPose(int pose) {
        this.actorDataMap.put(ActorDataTypes.POSE_INDEX, pose);
        final SetActorDataPacket packet = new SetActorDataPacket();
        packet.setTargetRuntimeID(this.getId());
        packet.setActorData(this.getActorDataMap());
        Server.getInstance().getOnlinePlayers().values().forEach(all -> all.sendPacket(packet));
    }

    private void markChunkChanged() {
        if (this.chunk != null) {
            this.chunk.setChanged();
        }
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.equipmentInventory != null) {
            this.nbt.putCompound(TAG_MAINHAND, ItemHelper.write(this.equipmentInventory.getItemInHand(), null));
            this.nbt.putCompound(TAG_OFFHAND, ItemHelper.write(this.equipmentInventory.getItemInOffhand(), null));
        }

        if (this.armorInventory != null) {
            ListTag<CompoundTag> armorTag = new ListTag<>(Tag.TAG_Compound);
            for (int i = 0; i < 4; i++) {
                armorTag.add(ItemHelper.write(this.armorInventory.getItem(i), i));
            }
            this.nbt.putList(TAG_ARMOR, armorTag);
        }

        this.nbt.putInt(TAG_POSE_INDEX, this.getPose());
    }

    @Override
    public void spawnTo(Player player) {
        super.spawnTo(player);
        if (this.equipmentInventory != null) {
            this.equipmentInventory.sendContents(player);
        }
        if (this.armorInventory != null) {
            this.armorInventory.sendContents(player);
        }
    }

    @Override
    public void spawnToAll() {
        if (this.chunk != null && !this.closed) {
            Collection<Player> chunkPlayers = this.level.getChunkPlayers(this.chunk.getX(), this.chunk.getZ()).values();
            for (Player chunkPlayer : chunkPlayers) {
                this.spawnTo(chunkPlayer);
            }
        }
    }

    @Override
    public void fall(float fallDistance) {
        super.fall(fallDistance);

        this.getLevel().addSound(this, Sound.MOB_ARMOR_STAND_LAND);
    }

    @Override
    public void kill() {
        super.kill();
        EntityDamageEvent lastDamageCause = this.lastDamageCause;
        boolean byAttack = lastDamageCause != null && lastDamageCause.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;

        Vector3 pos = getPosition();

        if (this.armorInventory != null) {
            pos.y += 0.2;
            level.dropItem(pos, armorInventory.getBoots());

            pos.y = y + 0.6;
            level.dropItem(pos, armorInventory.getLeggings());

            pos.y = y + 1.4;
            level.dropItem(pos, armorInventory.getChestplate());

            pos.y = y + 1.8;
            level.dropItem(pos, armorInventory.getHelmet());
            armorInventory.clearAll();
        }

        pos.y = y + 1.4;
        level.dropItem(byAttack ? pos : this, Item.get(ItemID.ARMOR_STAND));

        if (this.equipmentInventory != null) {
            equipmentInventory.getContents().values().forEach(items -> this.level.dropItem(this, items));
            equipmentInventory.clearAll();
        }

        level.addSound(this, Sound.MOB_ARMOR_STAND_BREAK);

        //todo: initiator should be a entity who kill it but not itself
        level.getVibrationManager().callVibrationEvent(new VibrationEvent(this.getLastDamageCause() instanceof EntityDamageByEntityEvent byEntity ? byEntity.getDamager() : this, this.getVector3(), VibrationType.ENTITY_DIE));
    }

    @Override
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
            final CompoundTag nbtMap = this.getNbt();
            if (nbtMap.getByte("InvulnerableTimer") > 0) {
                source.setCancelled(true);
            }
            if (super.attack(source)) {
                this.nbt.putByte("InvulnerableTimer", (byte) 9);
                return true;
            }
            return false;
        }

        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }
        setLastDamageCause(source);

        if (this.getHurtTicks() > 0) {
            setHealthCurrent(0);
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

        setDataProperty(ActorDataTypes.HURT, 9, true);
        level.addSound(this, Sound.MOB_ARMOR_STAND_HIT);

        return true;
    }

    @Override
    public String getOriginalName() {
        return "Armor Stand";
    }

    @Override
    public Set<String> typeFamily() {
        return Set.of("armor_stand", "inanimate", "mob");
    }

    @Override
    public boolean entityBaseTick(int tickDiff) {
        boolean hasUpdate = super.entityBaseTick(tickDiff);

        int hurtTime = this.getHurtTicks();
        if (hurtTime > 0 && age % 2 == 0) {
            setDataProperty(ActorDataTypes.HURT, hurtTime - 1, true);
            hasUpdate = true;
        }
        final CompoundTag nbtMap = this.getNbt();
        hurtTime = nbtMap.getByte("InvulnerableTimer");
        if (hurtTime > 0 && age % 2 == 0) {
            this.nbt.putByte("InvulnerableTimer", (byte) (hurtTime - 1));
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
    public boolean onUpdate(int currentTick) {
        int tickDiff = currentTick - lastUpdate;
        boolean hasUpdated = super.onUpdate(currentTick);

        if (closed || tickDiff <= 0 && !justCreated) {
            return hasUpdated;
        }

        lastUpdate = currentTick;

        boolean hasUpdate = entityBaseTick(tickDiff);

        if (isAlive()) {
            if (getHealthCurrent() < getHealthMax()) {
                setHealthCurrent(getHealthCurrent() + 0.001f);
            }
            motionY -= getGravity();

            double highestPosition = this.highestPosition;
            move(motionX, motionY, motionZ);

            float friction = 1 - getDrag();

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
    protected float getDrag() {
        if (hasWaterAt(getHeight() / 2f)) {
            return 0.25f;
        }
        return 0f;
    }

    @Override
    public String getInteractButtonText(Player player) {
        return "action.interact.armorstand.equip";
    }

    @Override
    public boolean canDoInteraction() {
        return true;
    }

    @Override
    public boolean canEquipByDispenser() {
        return true;
    }
}
