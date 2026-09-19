package org.powernukkitx.migration.steps;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.NumberTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.utils.ItemHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * Converts legacy PNX actor storage and relationships to the BDS-compatible 3.1.0 representation.
 *
 * @author Curse
 */
@Slf4j
public final class LevelDBActorV3_1_0Migration implements MigrationStep<LevelDBActorV3_1_0Migration.Data> {
    private static final String LEGACY_RIDING_UUID = "RidingUUID";

    @Override
    public MigrationFormat format() {
        return MigrationFormat.ACTOR;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public Data migrate(MigrationContext context, Data value) throws IOException {
        CompoundTag tag = value.actor();

        if (context.hasLegacyPositionTrackingRemap()) {
            PositionTrackingV3_1_0Migration.remapLegacyPnxItems(
                    tag,
                    context.getLegacyPositionTrackingSourceLastId(),
                    context.getLegacyPositionTrackingOffset()
            );
        }

        boolean legacyCreakingHeartLink = "minecraft:creaking".equals(tag.getString("identifier")) && tag.containsCompound("creakingHeart");

        migrateLegacyPnxActor(tag, value.dimensionId());

        if (!collectMigratedActorUuid(tag, value.actorUniqueId(), value.migratedActorUniqueIdsByUuid())) {
            return new Data(
                    tag,
                    value.dimensionId(),
                    value.actorUniqueId(),
                    value.migratedActorUniqueIdsByUuid(),
                    value.migratedRidingLinks(),
                    value.migratedCreakingLinks(),
                    false
            );
        }

        collectMigratedRidingLink(tag, value.actorUniqueId(), value.migratedRidingLinks());

        if (legacyCreakingHeartLink) {
            collectMigratedCreakingLink(tag, value.actorUniqueId(), value.migratedCreakingLinks());
        }

        tag.putLong("UniqueID", value.actorUniqueId());

        return new Data(
                tag,
                value.dimensionId(),
                value.actorUniqueId(),
                value.migratedActorUniqueIdsByUuid(),
                value.migratedRidingLinks(),
                value.migratedCreakingLinks(),
                true
        );
    }

    /**
     * Migrates one legacy PNX actor compound without dimension-specific relationship reconstruction.
     */
    public static void migrateLegacyPnxActor(CompoundTag tag) throws IOException {
        migrateLegacyPnxActor(tag, null);
    }

    private static void migrateLegacyPnxActor(CompoundTag tag, Integer dimensionId) throws IOException {
        normalizeActorFloatList(tag, "Pos", 3);
        normalizeActorFloatList(tag, "Motion", 3);
        normalizeActorFloatList(tag, "Rotation", 2);
        migrateLegacyActorUuid(tag);
        migrateLegacyActorParent(tag);
        migrateLegacyActorHealth(tag);
        migrateLegacyActorSaddled(tag);
        migrateLegacyBoatVariant(tag);
        migrateLegacyActorProperties(tag);
        migrateLegacyActorPersistence(tag);
        migrateLegacyActorScale(tag);
        migrateLegacyActorEquipment(tag);
        migrateLegacyPnxItemActor(tag);
        migrateLegacyPnxXpOrb(tag);
        migrateLegacyPnxArrow(tag);
        if ("minecraft:painting".equals(tag.getString("identifier"))) {
            migrateLegacyPnxPainting(tag);
        }

        if ("minecraft:creaking".equals(tag.getString("identifier"))) {
            migrateLegacyPnxCreaking(tag, dimensionId);
        }
    }

    private static void migrateLegacyActorUuid(CompoundTag tag) throws IOException {
        if ("minecraft:player".equals(tag.getString("identifier"))) return;

        if (!tag.contains("uuid")) return;

        if (!tag.containsString("uuid")) {
            throw new IOException("Legacy actor uuid is not a string for " + tag.getString("identifier"));
        }

        String legacyUuidValue = tag.getString("uuid");
        if (legacyUuidValue.isBlank()) {
            tag.remove("uuid");
            return;
        }

        UUID legacyUuid;
        try {
            legacyUuid = UUID.fromString(legacyUuidValue);
        } catch (IllegalArgumentException e) {
            throw new IOException("Legacy actor has invalid uuid '" + legacyUuidValue + "' for " + tag.getString("identifier"), e);
        }

        CompoundTag customData = tag.containsCompound(Entity.NBT_PNX_CUSTOM) ? tag.getCompound(Entity.NBT_PNX_CUSTOM).copy() : new CompoundTag();
        if (customData.contains(Entity.PNX_CUSTOM_UUID)) {
            if (!customData.containsString(Entity.PNX_CUSTOM_UUID)) {
                throw new IOException("Custom actor uuid is not a string for " + tag.getString("identifier"));
            }

            String customUuidValue = customData.getString(Entity.PNX_CUSTOM_UUID);
            UUID customUuid;
            try {
                customUuid = UUID.fromString(customUuidValue);
            } catch (IllegalArgumentException e) {
                throw new IOException("Custom actor has invalid uuid '" + customUuidValue + "' for " + tag.getString("identifier"), e);
            }

            if (!legacyUuid.equals(customUuid)) {
                throw new IOException("Actor UUID conflict for " + tag.getString("identifier") + ": legacy=" + legacyUuid + ", custom=" + customUuid);
            }
        } else {
            customData.putString(Entity.PNX_CUSTOM_UUID, legacyUuid.toString());
        }

        tag.putCompound(Entity.NBT_PNX_CUSTOM, customData);
        tag.remove("uuid");
    }

    private static void migrateLegacyActorParent(CompoundTag tag) {
        tag.remove("Parent");
    }

    private static void migrateLegacyActorHealth(CompoundTag tag) throws IOException {
        String identifier = tag.getString("identifier");
        if ("minecraft:item".equals(identifier) || "minecraft:xp_orb".equals(identifier)) return;

        Float currentHealth = null;
        if (tag.contains("Health")) {
            if (!tag.containsNumber("Health")) {
                throw new IOException("Legacy actor Health has unexpected tag type for " + identifier);
            }
            currentHealth = tag.getFloat("Health");
        }

        if (tag.contains("HealF")) {
            if (!tag.containsNumber("HealF")) {
                throw new IOException("Legacy actor HealF has unexpected tag type for " + identifier);
            }
            if (currentHealth == null) currentHealth = tag.getFloat("HealF");
        }

        if (currentHealth == null) return;
        if (!tag.containsList("Attributes", Tag.TAG_Compound)) {
            throw new IOException("Legacy actor has Health but no Attributes for " + identifier);
        }

        ListTag<CompoundTag> migratedAttributes = new ListTag<>();
        boolean foundHealth = false;
        for (CompoundTag attribute : tag.getList("Attributes", CompoundTag.class).getAll()) {
            CompoundTag migratedAttribute = attribute.copy();
            if ("minecraft:health".equals(migratedAttribute.getString("Name"))) {
                migratedAttribute.putFloat("Current", currentHealth);
                foundHealth = true;
            }
            migratedAttributes.add(migratedAttribute);
        }

        if (!foundHealth) {
            throw new IOException("Legacy actor has Health but no minecraft:health attribute for " + identifier);
        }

        tag.putList("Attributes", migratedAttributes);
        tag.remove("Health", "HealF");
    }

    private static void migrateLegacyActorSaddled(CompoundTag tag) throws IOException {
        if (tag.contains("Saddled") && !tag.containsNumber("Saddled")) {
            throw new IOException("Legacy actor Saddled has unexpected tag type for " + tag.getString("identifier"));
        }
        if (tag.contains("saddled") && !tag.containsNumber("saddled")) {
            throw new IOException("Legacy actor saddled has unexpected tag type for " + tag.getString("identifier"));
        }

        boolean saddled = tag.containsNumber("Saddled")
                ? tag.getBoolean("Saddled")
                : tag.containsNumber("saddled") && tag.getBoolean("saddled");
        tag.putBoolean("Saddled", saddled);
        tag.remove("saddled");
    }

    private static void migrateLegacyBoatVariant(CompoundTag tag) throws IOException {
        String identifier = tag.getString("identifier");
        if (!"minecraft:boat".equals(identifier) && !"minecraft:chest_boat".equals(identifier)) return;

        if (tag.contains("Variant") && !tag.containsNumber("Variant")) {
            throw new IOException("Legacy boat Variant has unexpected tag type for " + identifier);
        }
        if (tag.contains("woodID") && !tag.containsNumber("woodID")) {
            throw new IOException("Legacy boat woodID has unexpected tag type for " + identifier);
        }

        int variant = tag.containsNumber("Variant")
                ? tag.getInt("Variant")
                : tag.containsNumber("woodID") ? tag.getInt("woodID") : 0;
        tag.putInt("Variant", variant);
        tag.remove("woodID");
    }

    private static void migrateLegacyActorProperties(CompoundTag tag) {
        if (!tag.containsCompound("IntProperties") && !tag.containsCompound("FloatProperties")) return;

        CompoundTag properties = tag.containsCompound("properties") ? tag.getCompound("properties").copy() : new CompoundTag();
        for (var rootEntry : tag.getEntrySet()) {
            if (!"IntProperties".equals(rootEntry.getKey()) && !"FloatProperties".equals(rootEntry.getKey())) {
                continue;
            }

            if (!(rootEntry.getValue() instanceof CompoundTag legacyProperties)) {
                continue;
            }

            for (var propertyEntry : legacyProperties.getEntrySet()) {
                if (!properties.contains(propertyEntry.getKey())) {
                    properties.put(propertyEntry.getKey(), propertyEntry.getValue().copy());
                }
            }
        }

        tag.putCompound("properties", properties);
        tag.remove("IntProperties", "FloatProperties");
    }

    private static void migrateLegacyActorPersistence(CompoundTag tag) throws IOException {
        if (!tag.contains("Despawnable")) return;
        if (!tag.containsNumber("Despawnable")) {
            throw new IOException("Legacy actor Despawnable has unexpected tag type");
        }

        if (!tag.getBoolean("Despawnable")) tag.putBoolean("Persistent", true);
        else tag.remove("Persistent");
        tag.remove("Despawnable");
    }

    private static void migrateLegacyActorScale(CompoundTag tag) throws IOException {
        if (!tag.contains("Scale")) return;
        if (!tag.containsNumber("Scale")) {
            throw new IOException("Legacy actor Scale has unexpected tag type");
        }

        if (tag.getFloat("Scale") == 1f) tag.remove("Scale");
    }

    private static void migrateLegacyActorEquipment(CompoundTag tag) throws IOException {
        if (!tag.contains("Mainhand") && !tag.contains("Offhand") && !tag.contains("Armor")) return;

        tag.putList("Mainhand", migrateLegacyHandEquipment(tag, "Mainhand"));
        tag.putList("Offhand", migrateLegacyHandEquipment(tag, "Offhand"));

        ListTag<CompoundTag> armor = new ListTag<>();
        for (int i = 0; i < 5; i++) armor.add(ItemHelper.write(Item.AIR));

        if (tag.contains("Armor")) {
            if (!tag.containsList("Armor", Tag.TAG_Compound)) {
                throw new IOException("Legacy actor Armor has unexpected tag type for " + tag.getString("identifier"));
            }

            ListTag<CompoundTag> legacyArmor = tag.getList("Armor", CompoundTag.class);
            for (int i = 0; i < legacyArmor.size(); i++) {
                CompoundTag previous = legacyArmor.get(i);
                int slot = i;
                if (previous.contains("Slot")) {
                    if (!previous.containsNumber("Slot")) {
                        throw new IOException("Legacy actor Armor Slot has unexpected tag type for " + tag.getString("identifier"));
                    }
                    slot = previous.getByte("Slot");
                }

                if (slot < 0 || slot >= 5) {
                    throw new IOException("Legacy actor Armor Slot is out of range for " + tag.getString("identifier") + ": " + slot);
                }
                armor.add(slot, migrateLegacyEquipmentItem(previous));
            }
        }

        tag.putList("Armor", armor);
    }

    private static ListTag<CompoundTag> migrateLegacyHandEquipment(CompoundTag tag, String name) throws IOException {
        ListTag<CompoundTag> result = new ListTag<>();
        if (!tag.contains(name)) {
            result.add(ItemHelper.write(Item.AIR));
            return result;
        }

        CompoundTag previous;
        if (tag.containsCompound(name)) {
            previous = tag.getCompound(name);
        } else if (tag.containsList(name, Tag.TAG_Compound)) {
            ListTag<CompoundTag> list = tag.getList(name, CompoundTag.class);
            previous = list.size() > 0 ? list.get(0) : null;
        } else {
            throw new IOException("Legacy actor " + name + " has unexpected tag type for " + tag.getString("identifier"));
        }

        result.add(previous == null ? ItemHelper.write(Item.AIR) : migrateLegacyEquipmentItem(previous));
        return result;
    }

    private static CompoundTag migrateLegacyEquipmentItem(CompoundTag previous) {
        return ItemHelper.write(ItemHelper.read(previous.copy()), previous);
    }

    private static void migrateLegacyPnxItemActor(CompoundTag tag) throws IOException {
        if (!"minecraft:item".equals(tag.getString("identifier"))) {
            return;
        }

        if (tag.contains("Health") && !tag.containsNumber("Health")) {
            throw new IOException("Legacy minecraft:item Health has unexpected tag type");
        }

        if (tag.contains("Age") && !tag.containsNumber("Age")) {
            throw new IOException("Legacy minecraft:item Age has unexpected tag type");
        }

        if (tag.contains("OwnerID") && !tag.containsNumber("OwnerID")) {
            throw new IOException("Legacy minecraft:item OwnerID has unexpected tag type");
        }

        if (!tag.containsCompound("Item")) {
            throw new IOException("Legacy minecraft:item Item has unexpected tag type");
        }

        tag.putShort("Health", tag.containsNumber("Health") ? tag.getShort("Health") : 5);
        tag.putShort("Age", tag.containsNumber("Age") ? tag.getShort("Age") : 0);
        tag.putLong("OwnerID", tag.containsNumber("OwnerID") ? tag.getLong("OwnerID") : -1L);
        CompoundTag previousItem = tag.getCompound("Item").copy();
        tag.putCompound("Item", ItemHelper.write(ItemHelper.read(previousItem.copy()), previousItem));
        CompoundTag customData = getLegacyActorCustomData(tag);
        if (tag.contains("ShouldDespawn")) {
            if (!tag.containsNumber("ShouldDespawn")) {
                throw new IOException("Legacy minecraft:item ShouldDespawn has unexpected tag type");
            }

            if (!tag.getBoolean("ShouldDespawn")) {
                customData.putBoolean("ShouldDespawn", false);
            }

            tag.remove("ShouldDespawn");
        }

        if (tag.contains("DisplayOnly")) {
            if (!tag.containsNumber("DisplayOnly")) {
                throw new IOException("Legacy minecraft:item DisplayOnly has unexpected tag type");
            }

            if (tag.getBoolean("DisplayOnly")) {
                customData.putBoolean("DisplayOnly", true);
            }

            tag.remove("DisplayOnly");
        }

        if (tag.contains("Mergeable")) {
            if (!tag.containsNumber("Mergeable")) {
                throw new IOException("Legacy minecraft:item Mergeable has unexpected tag type");
            }

            if (!tag.getBoolean("Mergeable")) {
                customData.putBoolean("Mergeable", false);
            }

            tag.remove("Mergeable");
        }

        if (tag.contains("PickupDelay")) {
            if (!tag.containsNumber("PickupDelay")) {
                throw new IOException("Legacy minecraft:item PickupDelay has unexpected tag type");
            }

            int pickupDelay = tag.getInt("PickupDelay");
            if (pickupDelay == 32767) {
                pickupDelay = 65535;
            }

            if (pickupDelay != 0) {
                customData.putInt("PickupDelay", pickupDelay);
            }

            tag.remove("PickupDelay");
        }

        if (tag.contains("Owner") && !tag.containsString("Owner")) {
            throw new IOException("Legacy minecraft:item Owner has unexpected tag type");
        }

        if (tag.contains("Thrower") && !tag.containsString("Thrower")) {
            throw new IOException("Legacy minecraft:item Thrower has unexpected tag type");
        }

        String ownerName = tag.containsString("Owner") ? tag.getString("Owner") : "";
        if (ownerName.isBlank() && tag.containsString("Thrower")) {
            ownerName = tag.getString("Thrower");
        }

        if (!ownerName.isBlank()) {
            customData.putString("OwnerName", ownerName);
        }

        tag.remove("Owner", "Thrower");
        writeLegacyActorCustomData(tag, customData);
    }

    private static void migrateLegacyPnxXpOrb(CompoundTag tag) throws IOException {
        if (!"minecraft:xp_orb".equals(tag.getString("identifier"))) return;

        if (tag.contains("Age") && !tag.containsNumber("Age")) {
            throw new IOException("Legacy minecraft:xp_orb Age has unexpected tag type");
        }

        int experienceValue;
        if (tag.contains("experience value")) {
            if (!tag.containsNumber("experience value")) {
                throw new IOException(
                        "Legacy minecraft:xp_orb experience value has unexpected tag type");
            }

            experienceValue = tag.getInt("experience value");
        } else if (tag.contains("Value")) {
            if (!tag.containsNumber("Value")) {
                throw new IOException("Legacy minecraft:xp_orb Value has unexpected tag type");
            }

            experienceValue = tag.getInt("Value");
        } else {
            experienceValue = 1;
        }

        if (experienceValue <= 0) {
            experienceValue = 1;
        }

        tag.putShort("Age", tag.containsNumber("Age") ? tag.getShort("Age") : 0);
        tag.putInt("experience value", experienceValue);
        CompoundTag customData = getLegacyActorCustomData(tag);
        customData.remove("PickupDelay");
        writeLegacyActorCustomData(tag, customData);
        tag.remove("Health", "PickupDelay", "Value");
    }

    private static void migrateLegacyPnxArrow(CompoundTag tag) throws IOException {
        if (!"minecraft:arrow".equals(tag.getString("identifier"))) return;

        ListTag<CompoundTag> legacyEnchantments = new ListTag<>();
        if (tag.contains("ench")) {
            if (!tag.containsList("ench", Tag.TAG_Compound)) {
                throw new IOException("Legacy minecraft:arrow ench has unexpected tag type");
            }

            legacyEnchantments = tag.getList("ench", CompoundTag.class);
        }

        int enchantPower = getMigratedArrowEnchantment(tag, legacyEnchantments, "enchantPower", Enchantment.ID_BOW_POWER);
        int enchantPunch = getMigratedArrowEnchantment(tag, legacyEnchantments, "enchantPunch", Enchantment.ID_BOW_KNOCKBACK);
        int enchantFlame = getMigratedArrowEnchantment(tag, legacyEnchantments, "enchantFlame", Enchantment.ID_BOW_FLAME);
        int enchantInfinity = getMigratedArrowEnchantment(tag, legacyEnchantments, "enchantInfinity", Enchantment.ID_BOW_INFINITY);
        tag.putByte("enchantPower", enchantPower);
        tag.putByte("enchantPunch", enchantPunch);
        tag.putByte("enchantFlame", enchantFlame);
        tag.putByte("enchantInfinity", enchantInfinity);
        if (tag.contains("auxValue")) {
            if (!tag.containsNumber("auxValue")) {
                throw new IOException("Legacy minecraft:arrow auxValue has unexpected tag type");
            }

            tag.putByte("auxValue", tag.getByte("auxValue"));
        } else {
            tag.putByte("auxValue", 0);
        }

        if (tag.contains("OwnerID")) {
            if (!tag.containsNumber("OwnerID")) {
                throw new IOException("Legacy minecraft:arrow OwnerID has unexpected tag type");
            }

            tag.putLong("OwnerID", tag.getLong("OwnerID"));
        } else {
            tag.putLong("OwnerID", -1L);
        }

        int legacyPickup = 1;
        if (tag.contains("pickup")) {
            if (!tag.containsNumber("pickup")) {
                throw new IOException("Legacy minecraft:arrow pickup has unexpected tag type");
            }

            legacyPickup = tag.getByte("pickup");
        }

        if (tag.contains("player")) {
            if (!tag.containsNumber("player")) {
                throw new IOException("Legacy minecraft:arrow player has unexpected tag type");
            }

            tag.putByte("player", tag.getBoolean("player") ? 1 : 0);
        } else {
            boolean playerOwned =
                    switch (legacyPickup) {
                        case 0 -> false;
                        case 1 -> true;
                        case 2 -> enchantInfinity > 0;
                        default -> true;
                    };
            tag.putByte("player", playerOwned ? 1 : 0);
        }

        if (tag.contains("isCreative")) {
            if (!tag.containsNumber("isCreative")) {
                throw new IOException("Legacy minecraft:arrow isCreative has unexpected tag type");
            }

            tag.putByte("isCreative", tag.getBoolean("isCreative") ? 1 : 0);
        } else {
            tag.putByte("isCreative", 0);
        }

        if (legacyPickup < 0 || legacyPickup > 2) {
            CompoundTag customData = getLegacyActorCustomData(tag);
            customData.putInt("PickupMode", legacyPickup);
            writeLegacyActorCustomData(tag, customData);
        }

        tag.remove("Age", "pickup", "ench");
    }

    private static int getMigratedArrowEnchantment(CompoundTag tag, ListTag<CompoundTag> legacyEnchantments, String canonicalName, int enchantmentId) throws IOException {
        if (tag.contains(canonicalName)) {
            if (!tag.containsNumber(canonicalName)) {
                throw new IOException("Legacy minecraft:arrow " + canonicalName + " has unexpected tag type");
            }

            return Byte.toUnsignedInt(tag.getByte(canonicalName));
        }

        for (int i = 0; i < legacyEnchantments.size(); i++) {
            CompoundTag enchantment = legacyEnchantments.get(i);
            if (!enchantment.containsNumber("id") || !enchantment.containsNumber("lvl")) {
                throw new IOException("Legacy minecraft:arrow enchantment has invalid id or level");
            }

            if (enchantment.getInt("id") == enchantmentId) {
                return enchantment.getInt("lvl");
            }
        }

        return 0;
    }

    private static CompoundTag getLegacyActorCustomData(CompoundTag tag) throws IOException {
        if (!tag.contains(Entity.NBT_PNX_CUSTOM)) {
            return new CompoundTag();
        }

        if (!tag.containsCompound(Entity.NBT_PNX_CUSTOM)) {
            throw new IOException("Legacy actor PNXCustom has unexpected tag type for " + tag.getString("identifier"));
        }

        return tag.getCompound(Entity.NBT_PNX_CUSTOM).copy();
    }

    private static void writeLegacyActorCustomData(CompoundTag tag, CompoundTag customData) {
        if (customData.isEmpty()) {
            tag.remove(Entity.NBT_PNX_CUSTOM);
            return;
        }

        tag.putCompound(Entity.NBT_PNX_CUSTOM, customData);
    }

    private static void migrateLegacyPnxPainting(CompoundTag tag) {
        if (tag.contains("Motive")) {
            tag.putString("Motif", tag.getString("Motive"));
            tag.remove("Motive");
        }

        tag.remove("TileX");
        tag.remove("TileY");
        tag.remove("TileZ");
    }

    private static void migrateLegacyPnxCreaking(CompoundTag tag, Integer dimensionId)
            throws IOException {
        if (!tag.contains("creakingHeart")) {
            return;
        }

        if (!tag.containsCompound("creakingHeart")) {
            throw new IOException("Legacy Creaking has unexpected creakingHeart tag type");
        }

        if (dimensionId == null) {
            throw new IOException("Legacy Creaking Heart binding has no dimension context");
        }

        CompoundTag heart = tag.getCompound("creakingHeart");
        if (!heart.containsNumber("x") || !heart.containsNumber("y") || !heart.containsNumber("z")) {
            throw new IOException("Legacy Creaking Heart binding has invalid coordinates");
        }

        ListTag<FloatTag> homePos = new ListTag<>();
        homePos.add(new FloatTag(heart.getInt("x")));
        homePos.add(new FloatTag(heart.getInt("y")));
        homePos.add(new FloatTag(heart.getInt("z")));
        tag.putList("HomePos", homePos);
        tag.putInt("HomeDimensionId", dimensionId);
        tag.putBoolean("Persistent", true);
        tag.remove("creakingHeart");
    }

    private static void normalizeActorFloatList(CompoundTag tag, String name, int expectedSize) throws IOException {
        if (!tag.contains(name)) return;

        ListTag<? extends Tag> source = tag.getList(name);
        if (source.size() != expectedSize) {
            throw new IOException("Invalid entity " + name + " list size: expected " + expectedSize + ", got " + source.size());
        }

        boolean alreadyCanonical = true;
        for (Tag value : source.getAll()) {
            if (!(value instanceof NumberTag<?>)) {
                throw new IOException("Invalid entity " + name + " list element type: " + value.getClass().getSimpleName());
            }

            if (!(value instanceof FloatTag)) {
                alreadyCanonical = false;
            }
        }

        if (alreadyCanonical) return;

        ListTag<FloatTag> target = new ListTag<>();
        for (Tag value : source.getAll()) {
            NumberTag<?> number = (NumberTag<?>) value;
            target.add(new FloatTag(number.getData().floatValue()));
        }

        tag.putList(name, target);
    }

    private static boolean collectMigratedActorUuid(
            CompoundTag tag,
            long actorUniqueId,
            Map<UUID, Long> migratedActorUniqueIdsByUuid
    ) throws IOException {
        if (!tag.containsCompound(Entity.NBT_PNX_CUSTOM)) return true;

        CompoundTag customData = tag.getCompound(Entity.NBT_PNX_CUSTOM);
        if (!customData.contains(Entity.PNX_CUSTOM_UUID)) return true;

        if (!customData.containsString(Entity.PNX_CUSTOM_UUID)) {
            throw new IOException("Migrated actor custom UUID is not a string for " + tag.getString("identifier"));
        }

        String uuidValue = customData.getString(Entity.PNX_CUSTOM_UUID);
        if (uuidValue.isBlank()) return true;

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidValue);
        } catch (IllegalArgumentException e) {
            throw new IOException(
                    "Migrated actor has invalid custom UUID '"
                            + uuidValue
                            + "' for "
                            + tag.getString("identifier"),
                    e);
        }

        Long previous = migratedActorUniqueIdsByUuid.putIfAbsent(uuid, actorUniqueId);
        if (previous != null && previous != actorUniqueId) {
            ListTag<FloatTag> pos = tag.getList("Pos", FloatTag.class);
            log.warn(
                    "[LevelDB Migration] Discarding duplicate legacy actor {} UUID {} at {},{},{}: keeping ActorUniqueID {}, discarding ActorUniqueID {}",
                    tag.getString("identifier"),
                    uuid,
                    pos.get(0).data,
                    pos.get(1).data,
                    pos.get(2).data,
                    previous,
                    actorUniqueId);
            return false;
        }

        return true;
    }

    private static void collectMigratedRidingLink(
            CompoundTag tag,
            long riderUniqueId,
            List<LegacyRidingLink> migratedRidingLinks
    ) throws IOException {
        if (!tag.contains(LEGACY_RIDING_UUID)) return;

        if (!tag.containsString(LEGACY_RIDING_UUID)) {
            throw new IOException("Legacy RidingUUID is not a string for " + tag.getString("identifier"));
        }

        String vehicleUuidValue = tag.getString(LEGACY_RIDING_UUID);
        tag.remove(LEGACY_RIDING_UUID);
        if (vehicleUuidValue.isBlank()) return;

        UUID vehicleUuid;
        try {
            vehicleUuid = UUID.fromString(vehicleUuidValue);
        } catch (IllegalArgumentException e) {
            throw new IOException("Legacy actor has invalid RidingUUID '" + vehicleUuidValue + "' for " + tag.getString("identifier"), e);
        }

        migratedRidingLinks.add(new LegacyRidingLink(riderUniqueId, vehicleUuid));
    }

    private static void collectMigratedCreakingLink(
            CompoundTag tag,
            long actorUniqueId,
            Map<CreakingHeartPosition, Long> migratedCreakingLinks
    ) throws IOException {
        ListTag<FloatTag> home = tag.getList("HomePos", FloatTag.class);
        if (home.size() != 3) {
            throw new IOException("Migrated Creaking has invalid HomePos size: " + home.size());
        }

        CreakingHeartPosition position = new CreakingHeartPosition(
                (int) home.get(0).data,
                (int) home.get(1).data,
                (int) home.get(2).data
        );
        Long previous = migratedCreakingLinks.putIfAbsent(position, actorUniqueId);
        if (previous != null && previous != actorUniqueId) {
            throw new IOException("Multiple migrated Creakings reference Heart at "
                    + position.x() + "," + position.y() + "," + position.z());
        }
    }

    /**
     * Carries one actor and its cross-record migration state through the actor migration step.
     */
    public record Data(
            CompoundTag actor,
            int dimensionId,
            long actorUniqueId,
            Map<UUID, Long> migratedActorUniqueIdsByUuid,
            List<LegacyRidingLink> migratedRidingLinks,
            Map<CreakingHeartPosition, Long> migratedCreakingLinks,
            boolean keep
    ) {
    }

    /**
     * Represents one legacy rider-to-vehicle UUID relationship awaiting reconciliation.
     */
    public record LegacyRidingLink(long riderUniqueId, UUID vehicleUuid) {
    }

    /**
     * Represents the persisted block position of a Creaking Heart.
     */
    public record CreakingHeartPosition(int x, int y, int z) {
    }
}
