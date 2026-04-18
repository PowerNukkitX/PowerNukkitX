package cn.nukkit.entity.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityInteractable;
import cn.nukkit.entity.components.NameableComponent;
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
import cn.nukkit.utils.ItemHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorDataTypes;
import org.cloudburstmc.protocol.bedrock.packet.SetActorDataPacket;
import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Slf4j
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


    public EntityArmorStand(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        setHealthMax(6);
        setHealthCurrent(6);

        if (nbt.containsKey(TAG_POSE_INDEX)) {
            this.setPose(nbt.getInt(TAG_POSE_INDEX));
        }
    }

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
    public float getHeight() {
        return 1.975f;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
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

        if (this.namedTag.containsKey(TAG_MAINHAND)) {
            final Item mainhand = ItemHelper.read(this.namedTag.getCompound(TAG_MAINHAND));
            this.equipmentInventory.setItemInHand(mainhand, true);
            if (mainhand != null && !mainhand.isNull()) {
                log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} initEntity: loaded mainhand {} x{}",
                        (int) x, (int) y, (int) z, mainhand.getId(), mainhand.getCount());
            }
        }

        if (this.namedTag.containsKey(TAG_OFFHAND)) {
            final Item offhand = ItemHelper.read(this.namedTag.getCompound(TAG_OFFHAND));
            this.equipmentInventory.setItemInOffhand(offhand, true);
            // [ITEM_DEBUG]
            if (offhand != null && !offhand.isNull()) {
                log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} initEntity: loaded offhand {} x{}",
                        (int) x, (int) y, (int) z, offhand.getId(), offhand.getCount());
            }
        }

        if (this.namedTag.containsKey(TAG_ARMOR)) {
            List<NbtMap> armorList = this.namedTag.getList(TAG_ARMOR, NbtType.COMPOUND);
            for (NbtMap armorTag : armorList) {
                final int slot = armorTag.getByte("Slot");
                final Item armorItem = ItemHelper.read(armorTag);
                this.armorInventory.setItem(slot, armorItem);
                // [ITEM_DEBUG]
                if (armorItem != null && !armorItem.isNull()) {
                    log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} initEntity: loaded armor slot {} = {} x{}",
                            (int) x, (int) y, (int) z, slot, armorItem.getId(), armorItem.getCount());
                }
            }
        }

        if (this.namedTag.containsKey(TAG_POSE_INDEX)) {
            this.setPose(this.namedTag.getInt(TAG_POSE_INDEX));
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
            return false; // Returning true would consume the item
        }

        //Inventory
        boolean isArmor;

        boolean hasItemInHand = !item.isNull();
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
            // [ITEM_DEBUG]
            log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} equip: player {} added {} x1 to {}[{}]",
                    (int) x, (int) y, (int) z, player.getName(), handItem.getId(),
                    isArmorSlot ? "armor" : "equipment", slot);
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

            // [ITEM_DEBUG] Removing/swapping item from the armor stand
            log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} swap: player {} removing {} x{} from {}[{}], replacing with {}",
                    (int) x, (int) y, (int) z, player.getName(), item.getId(), item.getCount(),
                    isArmorSlot ? "armor" : "equipment", slot,
                    itemtoAddToArmorStand.isNull() ? "AIR" : itemtoAddToArmorStand.getId());
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
        return this.entityDataMap.get(ActorDataTypes.POSE_INDEX);
    }

    private void setPose(int pose) {
        this.entityDataMap.put(ActorDataTypes.POSE_INDEX, pose);
        final SetActorDataPacket packet = new SetActorDataPacket();
        packet.setTargetRuntimeID(this.getId());
        packet.setActorData(this.getEntityDataMap());
        Server.getInstance().getOnlinePlayers().values().forEach(all -> all.dataPacket(packet));
    }

    @Override
    public void saveNBT() {
        super.saveNBT();

        if (this.equipmentInventory != null) {
            this.namedTag.put(TAG_MAINHAND, ItemHelper.write(this.equipmentInventory.getItemInHand(), null));
            this.namedTag.put(TAG_OFFHAND, ItemHelper.write(this.equipmentInventory.getItemInOffhand(), null));
        } else {
            // [ITEM_DEBUG] This means items in hand/offhand will NOT be saved
            log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} saveNBT: equipmentInventory is NULL, hand items will not be saved!",
                    (int) x, (int) y, (int) z);
        }

        if (this.armorInventory != null) {
            List<NbtMap> armorTag = new ObjectArrayList<>();
            for (int i = 0; i < 4; i++) {
                armorTag.add(ItemHelper.write(this.armorInventory.getItem(i), i));
            }
            this.namedTag = this.namedTag.toBuilder().putList(TAG_ARMOR, NbtType.COMPOUND, armorTag).build();
        } else {
            // [ITEM_DEBUG] This means armor will NOT be saved
            log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} saveNBT: armorInventory is NULL, armor will not be saved!",
                    (int) x, (int) y, (int) z);
        }

        this.namedTag = this.namedTag.toBuilder().putInt(TAG_POSE_INDEX, this.getPose()).build();
    }

    @Override
    public void close() {
        // [ITEM_DEBUG] Log when an armor stand is closed while still holding items
        if (!this.closed) {
            boolean hasItems = false;
            StringBuilder items = new StringBuilder();
            if (this.equipmentInventory != null) {
                for (var entry : this.equipmentInventory.getContents().entrySet()) {
                    if (!entry.getValue().isNull()) {
                        hasItems = true;
                        items.append("equip[").append(entry.getKey()).append("]=")
                                .append(entry.getValue().getId()).append("x").append(entry.getValue().getCount()).append(", ");
                    }
                }
            }
            if (this.armorInventory != null) {
                for (int i = 0; i < 4; i++) {
                    Item armorItem = this.armorInventory.getItem(i);
                    if (!armorItem.isNull()) {
                        hasItems = true;
                        items.append("armor[").append(i).append("]=")
                                .append(armorItem.getId()).append("x").append(armorItem.getCount()).append(", ");
                    }
                }
            }
            if (hasItems) {
                log.debug("[ITEM_DEBUG] ArmorStand at {},{},{} CLOSE with items still present: {}. Stack trace:",
                        (int) x, (int) y, (int) z, items,
                        new Throwable("ArmorStand close trace"));
            }
        }
        super.close();
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
            if (namedTag.getByte("InvulnerableTimer") > 0) {
                source.setCancelled(true);
            }
            if (super.attack(source)) {
                this.namedTag = namedTag.toBuilder().putByte("InvulnerableTimer", (byte) 9).build();
                return true;
            }
            return false;
        }

        getServer().getPluginManager().callEvent(source);
        if (source.isCancelled()) {
            return false;
        }
        setLastDamageCause(source);

        if (getDataProperty(ActorDataTypes.HURT) > 0) {
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

        int hurtTime = getDataProperty(ActorDataTypes.HURT);
        if (hurtTime > 0 && age % 2 == 0) {
            setDataProperty(ActorDataTypes.HURT, hurtTime - 1, true);
            hasUpdate = true;
        }
        hurtTime = namedTag.getByte("InvulnerableTimer");
        if (hurtTime > 0 && age % 2 == 0) {
            this.namedTag = namedTag.toBuilder().putByte("InvulnerableTimer", (byte) (hurtTime - 1)).build();
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
