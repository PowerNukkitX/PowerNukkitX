package cn.nukkit.entity;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXDifference;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.data.IntPositionEntityData;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.inventory.FakeHumanEnderChestInventory;
import cn.nukkit.inventory.FakeHumanInventory;
import cn.nukkit.inventory.FakeHumanOffhandInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import cn.nukkit.utils.*;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 用来提供给插件基础，以方便的使用带有智能的EntityHuman
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public class EntityIntelligentHuman extends EntityIntelligent implements InventoryHolder {
    public static final int DATA_PLAYER_FLAG_SLEEP = 1;
    public static final int DATA_PLAYER_FLAG_DEAD = 2;
    public static final int DATA_PLAYER_FLAGS = 26;
    public static final int DATA_PLAYER_BED_POSITION = 28;
    public static final int DATA_PLAYER_BUTTON_TEXT = 40;

    public EntityIntelligentHuman(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected UUID uuid;

    protected byte[] rawUUID;

    protected Skin skin;

    protected FakeHumanInventory inventory;

    protected FakeHumanEnderChestInventory enderChestInventory;

    protected FakeHumanOffhandInventory offhandInventory;

    @Since("1.5.1.0-PN")
    @PowerNukkitOnly
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

    @Override
    public int getNetworkId() {
        return -1;
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

    public byte[] getRawUniqueId() {
        return rawUUID;
    }

    @Override
    public FakeHumanInventory getInventory() {
        return inventory;
    }

    public FakeHumanEnderChestInventory getEnderChestInventory() {
        return enderChestInventory;
    }

    public FakeHumanOffhandInventory getOffhandInventory() {
        return offhandInventory;
    }

    @Override
    protected void initEntity() {
        //EntityHuman
        this.setDataFlag(DATA_PLAYER_FLAGS, DATA_PLAYER_FLAG_SLEEP, false);
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_GRAVITY);

        this.setDataProperty(new IntPositionEntityData(DATA_PLAYER_BED_POSITION, 0, 0, 0), false);

        if (this.namedTag.contains("NameTag")) {
            this.setNameTag(this.namedTag.getString("NameTag"));
        }

        if (this.namedTag.contains("Skin") && this.namedTag.get("Skin") instanceof CompoundTag) {
            CompoundTag skinTag = this.namedTag.getCompound("Skin");
            if (!skinTag.contains("Transparent")) {
                skinTag.putBoolean("Transparent", false);
            }
            Skin newSkin = new Skin();
            if (skinTag.contains("ModelId")) {
                newSkin.setSkinId(skinTag.getString("ModelId"));
            }
            if (skinTag.contains("PlayFabId")) {
                newSkin.setPlayFabId(skinTag.getString("PlayFabId"));
            }
            if (skinTag.contains("Data")) {
                byte[] data = skinTag.getByteArray("Data");
                if (skinTag.contains("SkinImageWidth") && skinTag.contains("SkinImageHeight")) {
                    int width = skinTag.getInt("SkinImageWidth");
                    int height = skinTag.getInt("SkinImageHeight");
                    newSkin.setSkinData(new SerializedImage(width, height, data));
                } else {
                    newSkin.setSkinData(data);
                }
            }
            if (skinTag.contains("CapeId")) {
                newSkin.setCapeId(skinTag.getString("CapeId"));
            }
            if (skinTag.contains("CapeData")) {
                byte[] data = skinTag.getByteArray("CapeData");
                if (skinTag.contains("CapeImageWidth") && skinTag.contains("CapeImageHeight")) {
                    int width = skinTag.getInt("CapeImageWidth");
                    int height = skinTag.getInt("CapeImageHeight");
                    newSkin.setCapeData(new SerializedImage(width, height, data));
                } else {
                    newSkin.setCapeData(data);
                }
            }
            if (skinTag.contains("GeometryName")) {
                newSkin.setGeometryName(skinTag.getString("GeometryName"));
            }
            if (skinTag.contains("SkinResourcePatch")) {
                newSkin.setSkinResourcePatch(new String(skinTag.getByteArray("SkinResourcePatch"), StandardCharsets.UTF_8));
            }
            if (skinTag.contains("GeometryData")) {
                newSkin.setGeometryData(new String(skinTag.getByteArray("GeometryData"), StandardCharsets.UTF_8));
            }
            if (skinTag.contains("SkinAnimationData")) {
                newSkin.setAnimationData(new String(skinTag.getByteArray("SkinAnimationData"), StandardCharsets.UTF_8));
            } else if (skinTag.contains("AnimationData")) { // backwards compatible
                newSkin.setAnimationData(new String(skinTag.getByteArray("AnimationData"), StandardCharsets.UTF_8));
            }
            if (skinTag.contains("PremiumSkin")) {
                newSkin.setPremium(skinTag.getBoolean("PremiumSkin"));
            }
            if (skinTag.contains("PersonaSkin")) {
                newSkin.setPersona(skinTag.getBoolean("PersonaSkin"));
            }
            if (skinTag.contains("CapeOnClassicSkin")) {
                newSkin.setCapeOnClassic(skinTag.getBoolean("CapeOnClassicSkin"));
            }
            if (skinTag.contains("AnimatedImageData")) {
                ListTag<CompoundTag> list = skinTag.getList("AnimatedImageData", CompoundTag.class);
                for (CompoundTag animationTag : list.getAll()) {
                    float frames = animationTag.getFloat("Frames");
                    int type = animationTag.getInt("Type");
                    byte[] image = animationTag.getByteArray("Image");
                    int width = animationTag.getInt("ImageWidth");
                    int height = animationTag.getInt("ImageHeight");
                    int expression = animationTag.getInt("AnimationExpression");
                    newSkin.getAnimations().add(new SkinAnimation(new SerializedImage(width, height, image), type, frames, expression));
                }
            }
            if (skinTag.contains("ArmSize")) {
                newSkin.setArmSize(skinTag.getString("ArmSize"));
            }
            if (skinTag.contains("SkinColor")) {
                newSkin.setSkinColor(skinTag.getString("SkinColor"));
            }
            if (skinTag.contains("PersonaPieces")) {
                ListTag<CompoundTag> pieces = skinTag.getList("PersonaPieces", CompoundTag.class);
                for (CompoundTag piece : pieces.getAll()) {
                    newSkin.getPersonaPieces().add(new PersonaPiece(
                            piece.getString("PieceId"),
                            piece.getString("PieceType"),
                            piece.getString("PackId"),
                            piece.getBoolean("IsDefault"),
                            piece.getString("ProductId")
                    ));
                }
            }
            if (skinTag.contains("PieceTintColors")) {
                ListTag<CompoundTag> tintColors = skinTag.getList("PieceTintColors", CompoundTag.class);
                for (CompoundTag tintColor : tintColors.getAll()) {
                    newSkin.getTintColors().add(new PersonaPieceTint(
                            tintColor.getString("PieceType"),
                            tintColor.getList("Colors", StringTag.class).getAll().stream()
                                    .map(stringTag -> stringTag.data).collect(Collectors.toList())
                    ));
                }
            }
            if (skinTag.contains("IsTrustedSkin")) {
                newSkin.setTrusted(skinTag.getBoolean("IsTrustedSkin"));
            }
            this.setSkin(newSkin);
        }

        if (this.getSkin() == null) {
            this.setSkin(new Skin());
        }

        this.uuid = Utils.dataToUUID(String.valueOf(this.getId()).getBytes(StandardCharsets.UTF_8), this.getSkin()
                .getSkinData().data, this.getNameTag().getBytes(StandardCharsets.UTF_8));
        //EntityHumanType
        this.inventory = new FakeHumanInventory(this);
        if (namedTag.containsNumber("SelectedInventorySlot")) {
            this.inventory.setHeldItemIndex(NukkitMath.clamp(this.namedTag.getInt("SelectedInventorySlot"), 0, 8));
        }
        this.offhandInventory = new FakeHumanOffhandInventory(this);

        if (this.namedTag.contains("Inventory") && this.namedTag.get("Inventory") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("Inventory", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                int slot = item.getByte("Slot");
                if (slot >= 0 && slot < 9) { //hotbar
                    //Old hotbar saving stuff, remove it (useless now)
                    inventoryList.remove(item);
                } else if (slot >= 100 && slot < 104) {
                    this.inventory.setItem(this.inventory.getSize() + slot - 100, NBTIO.getItemHelper(item));
                } else if (slot == -106) {
                    this.offhandInventory.setItem(0, NBTIO.getItemHelper(item));
                } else {
                    this.inventory.setItem(slot - 9, NBTIO.getItemHelper(item));
                }
            }
        }

        this.enderChestInventory = new FakeHumanEnderChestInventory(this);

        if (this.namedTag.contains("EnderItems") && this.namedTag.get("EnderItems") instanceof ListTag) {
            ListTag<CompoundTag> inventoryList = this.namedTag.getList("EnderItems", CompoundTag.class);
            for (CompoundTag item : inventoryList.getAll()) {
                this.enderChestInventory.setItem(item.getByte("Slot"), NBTIO.getItemHelper(item));
            }
        }

        super.initEntity();
    }

    @Override
    public void updateMovement() {
        // 检测自由落体时间
        if (!this.onGround && this.y < this.highestPosition) {
            this.fallingTick++;
        }
        this.move(this.motionX, this.motionY, this.motionZ);
        broadcastMovement(false);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        //EntityHumanType
        ListTag<CompoundTag> inventoryTag = null;
        if (this.inventory != null) {
            inventoryTag = new ListTag<>("Inventory");
            this.namedTag.putList(inventoryTag);

            for (int slot = 0; slot < 9; ++slot) {
                inventoryTag.add(new CompoundTag()
                        .putByte("Count", 0)
                        .putShort("Damage", 0)
                        .putByte("Slot", slot)
                        .putByte("TrueSlot", -1)
                        .putShort("id", 0)
                );
            }

            int slotCount = Player.SURVIVAL_SLOTS + 9;
            for (int slot = 9; slot < slotCount; ++slot) {
                Item item = this.inventory.getItem(slot - 9);
                inventoryTag.add(NBTIO.putItemHelper(item, slot));
            }

            for (int slot = 100; slot < 104; ++slot) {
                Item item = this.inventory.getItem(this.inventory.getSize() + slot - 100);
                if (item != null && item.getId() != Item.AIR) {
                    inventoryTag.add(NBTIO.putItemHelper(item, slot));
                }
            }

            this.namedTag.putInt("SelectedInventorySlot", this.inventory.getHeldItemIndex());
        }

        if (this.offhandInventory != null) {
            Item item = this.offhandInventory.getItem(0);
            if (item.getId() != Item.AIR) {
                if (inventoryTag == null) {
                    inventoryTag = new ListTag<>("Inventory");
                    this.namedTag.putList(inventoryTag);
                }
                inventoryTag.add(NBTIO.putItemHelper(item, -106));
            }
        }

        this.namedTag.putList(new ListTag<CompoundTag>("EnderItems"));
        if (this.enderChestInventory != null) {
            for (int slot = 0; slot < 27; ++slot) {
                Item item = this.enderChestInventory.getItem(slot);
                if (item != null && item.getId() != Item.AIR) {
                    this.namedTag.getList("EnderItems", CompoundTag.class).add(NBTIO.putItemHelper(item, slot));
                }
            }
        }
        //EntityHuman
        if (skin != null) {
            CompoundTag skinTag = new CompoundTag()
                    .putByteArray("Data", this.getSkin().getSkinData().data)
                    .putInt("SkinImageWidth", this.getSkin().getSkinData().width)
                    .putInt("SkinImageHeight", this.getSkin().getSkinData().height)
                    .putString("ModelId", this.getSkin().getSkinId())
                    .putString("CapeId", this.getSkin().getCapeId())
                    .putByteArray("CapeData", this.getSkin().getCapeData().data)
                    .putInt("CapeImageWidth", this.getSkin().getCapeData().width)
                    .putInt("CapeImageHeight", this.getSkin().getCapeData().height)
                    .putByteArray("SkinResourcePatch", this.getSkin().getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("GeometryData", this.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
                    .putByteArray("SkinAnimationData", this.getSkin().getAnimationData().getBytes(StandardCharsets.UTF_8))
                    .putBoolean("PremiumSkin", this.getSkin().isPremium())
                    .putBoolean("PersonaSkin", this.getSkin().isPersona())
                    .putBoolean("CapeOnClassicSkin", this.getSkin().isCapeOnClassic())
                    .putString("ArmSize", this.getSkin().getArmSize())
                    .putString("SkinColor", this.getSkin().getSkinColor())
                    .putBoolean("IsTrustedSkin", this.getSkin().isTrusted());

            List<SkinAnimation> animations = this.getSkin().getAnimations();
            if (!animations.isEmpty()) {
                ListTag<CompoundTag> animationsTag = new ListTag<>("AnimatedImageData");
                for (SkinAnimation animation : animations) {
                    animationsTag.add(new CompoundTag()
                            .putFloat("Frames", animation.frames)
                            .putInt("Type", animation.type)
                            .putInt("ImageWidth", animation.image.width)
                            .putInt("ImageHeight", animation.image.height)
                            .putInt("AnimationExpression", animation.expression)
                            .putByteArray("Image", animation.image.data));
                }
                skinTag.putList(animationsTag);
            }

            List<PersonaPiece> personaPieces = this.getSkin().getPersonaPieces();
            if (!personaPieces.isEmpty()) {
                ListTag<CompoundTag> piecesTag = new ListTag<>("PersonaPieces");
                for (PersonaPiece piece : personaPieces) {
                    piecesTag.add(new CompoundTag().putString("PieceId", piece.id)
                            .putString("PieceType", piece.type)
                            .putString("PackId", piece.packId)
                            .putBoolean("IsDefault", piece.isDefault)
                            .putString("ProductId", piece.productId));
                }
            }

            List<PersonaPieceTint> tints = this.getSkin().getTintColors();
            if (!tints.isEmpty()) {
                ListTag<CompoundTag> tintsTag = new ListTag<>("PieceTintColors");
                for (PersonaPieceTint tint : tints) {
                    ListTag<StringTag> colors = new ListTag<>("Colors");
                    colors.setAll(tint.colors.stream().map(s -> new StringTag("", s)).collect(Collectors.toList()));
                    tintsTag.add(new CompoundTag()
                            .putString("PieceType", tint.pieceType)
                            .putList(colors));
                }
            }

            if (!this.getSkin().getPlayFabId().isEmpty()) {
                skinTag.putString("PlayFabId", this.getSkin().getPlayFabId());
            }

            this.namedTag.putCompound("Skin", skinTag);
        }
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
    protected boolean applyNameTag(@Nonnull Player player, @Nonnull Item item) {
        return false;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @PowerNukkitXDifference(since = "1.19.21-r4", info = "add EntityDamageEvent param to help cal the armor damage")
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

            if (armor.getId() == ItemID.SHIELD)
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

    @PowerNukkitOnly
    @Since("1.5.1.0-PN")
    @Override
    public String getOriginalName() {
        return "Human";
    }

    @Override
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
            pk.metadata = this.dataProperties;
            player.dataPacket(pk);

            this.inventory.sendArmorContents(player);
            this.offhandInventory.sendContents(player);

            if (this.riding != null) {
                SetEntityLinkPacket pkk = new SetEntityLinkPacket();
                pkk.vehicleUniqueId = this.riding.getId();
                pkk.riderUniqueId = this.getId();
                pkk.type = 1;
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

    @PowerNukkitOnly
    @Override
    protected void onBlock(Entity entity, EntityDamageEvent event, boolean animate) {
        super.onBlock(entity, event, animate);
        Item shield = getInventory().getItemInHand();
        Item shieldOffhand = getOffhandInventory().getItem(0);
        if (shield.getId() == ItemID.SHIELD) {
            shield = damageArmor(shield, entity, event);
            getInventory().setItemInHand(shield);
        } else if (shieldOffhand.getId() == ItemID.SHIELD) {
            shieldOffhand = damageArmor(shieldOffhand, entity, event);
            getOffhandInventory().setItem(0, shieldOffhand);
        }
    }
}
