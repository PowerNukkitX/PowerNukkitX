package org.powernukkitx.migration.steps;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Attribute;
import org.powernukkitx.migration.MigrationContext;
import org.powernukkitx.migration.MigrationFormat;
import org.powernukkitx.migration.MigrationStep;
import org.powernukkitx.migration.MigrationVersion;
import org.powernukkitx.migration.data.PlayerMigrationData;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.NumberTag;
import org.powernukkitx.nbt.tag.StringTag;
import org.powernukkitx.nbt.tag.Tag;

import java.util.Map;

/**
 * Converts legacy player storage fields and PNX supplemental data to the canonical 3.1.0 representation.
 *
 * @author Curse
 */
public final class PlayerV3_1_0Migration implements MigrationStep<PlayerMigrationData> {

    @Override
    public MigrationFormat format() {
        return MigrationFormat.PLAYER;
    }

    @Override
    public MigrationVersion targetVersion() {
        return MigrationVersion.V3_1_0;
    }

    @Override
    public PlayerMigrationData migrate(MigrationContext context, PlayerMigrationData value) {
        CompoundTag migrated = value.player().copy();
        CompoundTag pnxExtra = value.pnxExtra().copy();

        ensureCanonicalPlayerIdentity(migrated);
        migrateVectorToFloat(migrated, "Pos");
        migrateVectorToFloat(migrated, "Motion");
        migrateInventoryLayout(migrated);
        migrateProgression(migrated);
        migrateVitals(migrated);
        ensureCanonicalPlayerAttributes(migrated);
        migrateProperties(migrated);
        migrateRecipeUnlocking(migrated);
        migrateAbilitiesAndPermissions(migrated, pnxExtra);
        migratePnxOwnedRoots(migrated, pnxExtra);

        return new PlayerMigrationData(migrated, pnxExtra);
    }

    private static void ensureCanonicalPlayerIdentity(CompoundTag nbt) {
        if (!nbt.contains("definitions")) {
            nbt.putList("definitions", new ListTag<StringTag>(Tag.TAG_String)
                    .add(new StringTag("+minecraft:player")));
        }

        if (!nbt.contains("format_version")) {
            nbt.putString("format_version", "1.12.0");
        }

        if (!nbt.contains("identifier")) {
            nbt.putString("identifier", "minecraft:player");
        }
    }

    private static void migratePnxOwnedRoots(CompoundTag nbt, CompoundTag pnxExtra) {
        movePnxRoot(nbt, pnxExtra, "firstPlayed");
        movePnxRoot(nbt, pnxExtra, "lastPlayed");
        movePnxRoot(nbt, pnxExtra, "lastIP");
        movePnxRoot(nbt, pnxExtra, "UUIDMost");
        movePnxRoot(nbt, pnxExtra, "UUIDLeast");
        movePnxRoot(nbt, pnxExtra, "Level");
        movePnxRoot(nbt, pnxExtra, "SpawnLevel");
        movePnxRoot(nbt, pnxExtra, "SpawnBlockLevel");
        movePnxRoot(nbt, pnxExtra, "Achievements");
        movePnxRoot(nbt, pnxExtra, "NameTag");
        nbt.remove("Scale", "Despawnable");
        pnxExtra.remove("Scale", "Despawnable");
        movePnxRoot(nbt, pnxExtra, "CursorItem");
        movePnxRoot(nbt, pnxExtra, "BedrockMigrated");
        movePnxRoot(nbt, pnxExtra, "fogIdentifiers");
        movePnxRoot(nbt, pnxExtra, "userProvidedFogIds");
    }

    private static void movePnxRoot(CompoundTag nbt, CompoundTag pnxExtra, String name) {
        if (!nbt.contains(name)) {
            return;
        }

        if (!pnxExtra.contains(name)) {
            pnxExtra.put(name, nbt.get(name).copy());
        }

        nbt.remove(name);
    }

    private static void migrateVectorToFloat(CompoundTag nbt, String name) {
        if (!(nbt.get(name) instanceof ListTag<?> list) || list.size() != 3) {
            return;
        }

        if (list.get(0) instanceof FloatTag && list.get(1) instanceof FloatTag && list.get(2) instanceof FloatTag) {
            return;
        }

        if (!(list.get(0) instanceof NumberTag<?> x) ||
            !(list.get(1) instanceof NumberTag<?> y) ||
            !(list.get(2) instanceof NumberTag<?> z)) {
            return;
        }

        nbt.putList(name, new ListTag<FloatTag>()
                .add(new FloatTag(x.getData().floatValue()))
                .add(new FloatTag(y.getData().floatValue()))
                .add(new FloatTag(z.getData().floatValue())));
    }

    private static void migrateInventoryLayout(CompoundTag nbt) {
        if (!needsInventoryMigration(nbt)) {
            return;
        }

        ListTag<CompoundTag> legacyInventory = nbt.getList("Inventory", CompoundTag.class);
        ListTag<CompoundTag> inventory = new ListTag<>(Tag.TAG_Compound);
        ListTag<CompoundTag> armor = new ListTag<>(Tag.TAG_Compound);

        for (int slot = 0; slot < 36; slot++) {
            CompoundTag item = findSlot(legacyInventory, slot);
            inventory.add(item != null ? canonicalStorageItem(item, slot) : emptyStorageItem(slot));
        }

        ListTag<CompoundTag> existingArmor = nbt.containsList("Armor")
                ? nbt.getList("Armor", CompoundTag.class)
                : new ListTag<>(Tag.TAG_Compound);

        for (int slot = 0; slot < 5; slot++) {
            CompoundTag item = slot < existingArmor.size()
                    ? existingArmor.get(slot)
                    : slot < 4 ? findSlot(legacyInventory, 36 + slot) : null;

            armor.add(item != null ? canonicalStorageItem(item, null) : emptyStorageItem(null));
        }

        ListTag<CompoundTag> mainhand = new ListTag<>(Tag.TAG_Compound);
        int selectedSlot = Math.max(0, Math.min(8, nbt.getInt("SelectedInventorySlot")));
        CompoundTag selectedItem = inventory.get(selectedSlot);
        mainhand.add(canonicalStorageItem(selectedItem, null));

        ListTag<CompoundTag> offhand = new ListTag<>(Tag.TAG_Compound);
        if (nbt.containsCompound("OffInventory")) {
            offhand.add(canonicalStorageItem(nbt.getCompound("OffInventory"), null));
        } else {
            offhand.add(emptyStorageItem(null));
        }

        ListTag<CompoundTag> enderChest = new ListTag<>(Tag.TAG_Compound);
        ListTag<CompoundTag> legacyEnder = nbt.getList("EnderItems", CompoundTag.class);

        for (int slot = 0; slot < 27; slot++) {
            CompoundTag item = findSlot(legacyEnder, slot);
            enderChest.add(item != null ? canonicalStorageItem(item, slot) : emptyStorageItem(slot));
        }

        nbt.putList("Inventory", inventory);
        nbt.putList("Armor", armor);
        nbt.putList("Mainhand", mainhand);
        nbt.putList("Offhand", offhand);
        nbt.putList("EnderChestInventory", enderChest);

        nbt.remove("OffInventory");
        nbt.remove("EnderItems");
    }

    private static boolean needsInventoryMigration(CompoundTag nbt) {
        if (nbt.contains("OffInventory") || nbt.contains("EnderItems")) {
            return true;
        }

        if (!nbt.containsList("Armor") ||
            !nbt.containsList("Mainhand") ||
            !nbt.containsList("Offhand") ||
            !nbt.containsList("EnderChestInventory")) {
            return true;
        }

        ListTag<CompoundTag> inventory = nbt.getList("Inventory", CompoundTag.class);
        ListTag<CompoundTag> armor = nbt.getList("Armor", CompoundTag.class);

        if (inventory.size() != 36 || armor.size() != 5) {
            return true;
        }

        for (CompoundTag item : inventory.getAll()) {
            if ((item.getByte("Slot") & 0xff) >= 36) {
                return true;
            }
        }

        return false;
    }

    private static CompoundTag findSlot(ListTag<CompoundTag> items, int slot) {
        for (CompoundTag item : items.getAll()) {
            if ((item.getByte("Slot") & 0xff) == slot) {
                return item;
            }
        }

        return null;
    }

    private static CompoundTag canonicalStorageItem(CompoundTag source, Integer slot) {
        if (source.getByte("Count") <= 0 ||
            source.getString("Name").isBlank() ||
            "minecraft:air".equals(source.getString("Name"))) {
            return emptyStorageItem(slot);
        }

        CompoundTag item = source.copy();

        item.remove("version");

        if (!item.contains("WasPickedUp")) {
            item.putByte("WasPickedUp", 0);
        }

        if (slot != null) {
            item.putByte("Slot", slot);
        } else {
            item.remove("Slot");
        }

        return item;
    }

    private static CompoundTag emptyStorageItem(Integer slot) {
        CompoundTag item = new CompoundTag()
                .putByte("Count", 0)
                .putShort("Damage", 0)
                .putString("Name", "")
                .putByte("WasPickedUp", 0);

        if (slot != null) {
            item.putByte("Slot", slot);
        }

        return item;
    }

    private static void migrateVitals(CompoundTag nbt) {
        ListTag<CompoundTag> attributes = nbt.containsList("Attributes")
                ? nbt.getList("Attributes", CompoundTag.class)
                : new ListTag<>(Tag.TAG_Compound);

        ensureAttribute(
                attributes,
                Attribute.HEALTH,
                readNumeric(nbt, "Health", Attribute.getAttribute(Attribute.HEALTH).getDefaultValue()),
                nbt.contains("Health")
        );

        ensureAttribute(
                attributes,
                Attribute.ABSORPTION,
                readNumeric(nbt, "AbsorptionAmount", Attribute.getAttribute(Attribute.ABSORPTION).getDefaultValue()),
                nbt.contains("AbsorptionAmount")
        );

        ensureAttribute(
                attributes,
                Attribute.FOOD,
                readNumeric(nbt, "foodLevel", Attribute.getAttribute(Attribute.FOOD).getDefaultValue()),
                nbt.contains("foodLevel")
        );

        ensureAttribute(
                attributes,
                Attribute.SATURATION,
                readNumeric(nbt, "foodSaturationLevel", Attribute.getAttribute(Attribute.SATURATION).getDefaultValue()),
                nbt.contains("foodSaturationLevel")
        );

        ensureAttribute(
                attributes,
                Attribute.EXHAUSTION,
                readNumeric(nbt, "foodExhaustionLevel", Attribute.getAttribute(Attribute.EXHAUSTION).getDefaultValue()),
                nbt.contains("foodExhaustionLevel")
        );

        nbt.putList("Attributes", attributes);
        nbt.remove("Health", "AbsorptionAmount", "foodLevel", "foodSaturationLevel", "foodExhaustionLevel");
    }

    private static float readNumeric(CompoundTag nbt, String name, float defaultValue) {
        if (nbt.get(name) instanceof NumberTag<?> number) {
            return number.getData().floatValue();
        }

        return defaultValue;
    }

    private static void ensureAttribute(ListTag<CompoundTag> attributes, int attributeId, float value, boolean overwriteCurrent) {
        Attribute template = Attribute.getAttribute(attributeId);

        for (CompoundTag attribute : attributes.getAll()) {
            if (!template.getName().equals(attribute.getString("Name"))) {
                continue;
            }

            if (overwriteCurrent) {
                float min = attribute.containsFloat("Min") ? attribute.getFloat("Min") : template.getMinValue();
                float max = attribute.containsFloat("Max") ? attribute.getFloat("Max") : template.getMaxValue();
                attribute.putFloat("Current", Math.max(min, Math.min(max, value)));
            }

            return;
        }

        if (attributeId == Attribute.KNOCKBACK_RESISTANCE) {
            template.setMinValue(-2f)
                    .setDefaultMinimum(-2f);
        } else if (attributeId == Attribute.ATTACK_DAMAGE) {
            template.setMinValue(1f)
                    .setMaxValue(1f)
                    .setDefaultMinimum(1f)
                    .setDefaultMaximum(1f)
                    .setDefaultValue(1f);
        }

        template.setValue(value);
        attributes.add(Attribute.toNBT(template));
    }

    private static void ensureCanonicalPlayerAttributes(CompoundTag nbt) {
        ListTag<CompoundTag> attributes = nbt.containsList("Attributes")
                ? nbt.getList("Attributes", CompoundTag.class)
                : new ListTag<>(Tag.TAG_Compound);

        ensureAttribute(attributes, Attribute.FOLLOW_RANGE, Attribute.getAttribute(Attribute.FOLLOW_RANGE).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.KNOCKBACK_RESISTANCE, Attribute.getAttribute(Attribute.KNOCKBACK_RESISTANCE).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.MOVEMENT_SPEED, Attribute.getAttribute(Attribute.MOVEMENT_SPEED).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.UNDER_WATER_MOVEMENT_SPEED, Attribute.getAttribute(Attribute.UNDER_WATER_MOVEMENT_SPEED).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.LAVA_MOVEMENT_SPEED, Attribute.getAttribute(Attribute.LAVA_MOVEMENT_SPEED).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.ATTACK_DAMAGE, Attribute.getAttribute(Attribute.ATTACK_DAMAGE).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.LUCK, Attribute.getAttribute(Attribute.LUCK).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.FRICTION_MODIFIER, Attribute.getAttribute(Attribute.FRICTION_MODIFIER).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.BOUNCINESS, Attribute.getAttribute(Attribute.BOUNCINESS).getDefaultValue(), false);
        ensureAttribute(attributes, Attribute.AIR_DRAG_MODIFIER, Attribute.getAttribute(Attribute.AIR_DRAG_MODIFIER).getDefaultValue(), false);

        nbt.putList("Attributes", attributes);
    }

    private static void migrateProperties(CompoundTag nbt) {
        boolean hadCanonicalProperties = nbt.containsCompound("properties");
        boolean hasLegacyIntProperties = nbt.containsCompound("IntProperties");
        boolean hasLegacyFloatProperties = nbt.containsCompound("FloatProperties");

        if (!hadCanonicalProperties && !hasLegacyIntProperties && !hasLegacyFloatProperties) {
            return;
        }

        CompoundTag properties = hadCanonicalProperties
                ? nbt.getCompound("properties").copy()
                : new CompoundTag();

        for (Map.Entry<String, Tag> rootEntry : nbt.getEntrySet()) {
            String rootName = rootEntry.getKey();

            if (!"IntProperties".equals(rootName) && !"FloatProperties".equals(rootName)) {
                continue;
            }

            if (!(rootEntry.getValue() instanceof CompoundTag legacyProperties)) {
                continue;
            }

            for (Map.Entry<String, Tag> propertyEntry : legacyProperties.getEntrySet()) {
                if (!properties.contains(propertyEntry.getKey())) {
                    properties.put(propertyEntry.getKey(), propertyEntry.getValue().copy());
                }
            }
        }

        if (!properties.isEmpty() || hadCanonicalProperties) {
            nbt.putCompound("properties", properties);
        }

        nbt.remove("IntProperties", "FloatProperties");
    }

    private static void migrateRecipeUnlocking(CompoundTag nbt) {
        if (nbt.containsCompound("recipe_unlocking")) {
            nbt.remove("UnlockedRecipes");
            return;
        }

        if (!nbt.containsList("UnlockedRecipes")) {
            return;
        }

        ListTag<StringTag> unlockedRecipes = new ListTag<>(Tag.TAG_String);

        for (StringTag recipe : nbt.getList("UnlockedRecipes", StringTag.class).getAll()) {
            unlockedRecipes.add(new StringTag(recipe.parseValue()));
        }

        CompoundTag recipeUnlocking = new CompoundTag()
                .putList("unlocked_recipes", unlockedRecipes)
                .putInt("used_contexts", 2);

        nbt.putCompound("recipe_unlocking", recipeUnlocking);
        nbt.remove("UnlockedRecipes");
    }

    private static void migrateAbilitiesAndPermissions(CompoundTag nbt, CompoundTag pnxExtra) {
        CompoundTag legacyAbilities = nbt.containsCompound("Abilities")
                ? nbt.getCompound("Abilities")
                : new CompoundTag();

        migrateLegacyPnxAbilityExtras(legacyAbilities, pnxExtra);

        int playerPermissionsLevel = nbt.containsInt("playerPermissionsLevel")
                ? nbt.getInt("playerPermissionsLevel")
                : migrateLegacyPlayerPermission(nbt.getString("PlayerPermission"));

        int permissionsLevel = nbt.containsInt("permissionsLevel")
                ? nbt.getInt("permissionsLevel")
                : migrateLegacyCommandPermission(nbt.getString("CommandPermission"));

        if (!nbt.containsCompound("abilities")) {
            int gamemode = Player.fromStorageGamemode(nbt.getInt("PlayerGameMode"));
            boolean creative = gamemode == Player.CREATIVE;
            boolean spectator = gamemode == Player.SPECTATOR;
            boolean operator = readLegacyAbility(
                    legacyAbilities,
                    "OPERATOR",
                    playerPermissionsLevel == Player.PERMISSION_OPERATOR
            );

            CompoundTag abilities = new CompoundTag()
                    .putByte("attackmobs", readLegacyAbility(legacyAbilities, "ATTACK_MOBS", true) ? 1 : 0)
                    .putByte("attackplayers", readLegacyAbility(legacyAbilities, "ATTACK_PLAYERS", true) ? 1 : 0)
                    .putByte("build", readLegacyAbility(legacyAbilities, "BUILD", true) ? 1 : 0)
                    .putByte("doorsandswitches", readLegacyAbility(legacyAbilities, "DOORS_AND_SWITCHED", true) ? 1 : 0)
                    .putFloat("flySpeed", Player.DEFAULT_FLY_SPEED)
                    .putByte("flying", readLegacyAbility(legacyAbilities, "FLYING", spectator) ? 1 : 0)
                    .putByte("instabuild", creative ? 1 : 0)
                    .putByte("invulnerable", readLegacyAbility(legacyAbilities, "NO_MVP", false) ? 1 : 0)
                    .putByte("lightning", 0)
                    .putByte("mayfly", readLegacyAbility(legacyAbilities, "ALLOW_FLIGHT", creative || spectator) ? 1 : 0)
                    .putByte("mine", readLegacyAbility(legacyAbilities, "MINE", true) ? 1 : 0)
                    .putByte("op", operator ? 1 : 0)
                    .putByte("opencontainers", readLegacyAbility(legacyAbilities, "OPEN_CONTAINERS", true) ? 1 : 0)
                    .putByte("teleport", readLegacyAbility(legacyAbilities, "TELEPORT", operator) ? 1 : 0)
                    .putFloat("verticalFlySpeed", 1f)
                    .putFloat("walkSpeed", Player.DEFAULT_SPEED);

            nbt.putCompound("abilities", abilities);
        }

        nbt.putInt("permissionsLevel", permissionsLevel);
        nbt.putInt("playerPermissionsLevel", playerPermissionsLevel);
        nbt.remove("Abilities", "PlayerPermission", "CommandPermission");
    }

    private static void migrateLegacyPnxAbilityExtras(CompoundTag legacyAbilities, CompoundTag pnxExtra) {
        if (legacyAbilities.isEmpty()) {
            return;
        }

        CompoundTag adventureSettings = pnxExtra.containsCompound("AdventureSettings")
                ? pnxExtra.getCompound("AdventureSettings").copy()
                : new CompoundTag();

        for (Map.Entry<String, Tag> entry : legacyAbilities.getEntrySet()) {
            if (isBdsPersistedLegacyAbility(entry.getKey())) {
                continue;
            }

            if (!adventureSettings.contains(entry.getKey())) {
                adventureSettings.put(entry.getKey(), entry.getValue().copy());
            }
        }

        if (!adventureSettings.isEmpty()) {
            pnxExtra.putCompound("AdventureSettings", adventureSettings);
        }
    }

    private static boolean isBdsPersistedLegacyAbility(String name) {
        return switch (name) {
            case "ATTACK_MOBS",
                 "ATTACK_PLAYERS",
                 "BUILD",
                 "DOORS_AND_SWITCHED",
                 "FLYING",
                 "NO_MVP",
                 "ALLOW_FLIGHT",
                 "MINE",
                 "OPERATOR",
                 "OPEN_CONTAINERS",
                 "TELEPORT" -> true;
            default -> false;
        };
    }

    private static boolean readLegacyAbility(CompoundTag abilities, String name, boolean defaultValue) {
        if (abilities.get(name) instanceof NumberTag<?> number) {
            return number.getData().intValue() != 0;
        }

        return defaultValue;
    }

    private static int migrateLegacyPlayerPermission(String permission) {
        return switch (permission) {
            case "VISITOR" -> Player.PERMISSION_VISITOR;
            case "OPERATOR" -> Player.PERMISSION_OPERATOR;
            case "CUSTOM" -> Player.PERMISSION_CUSTOM;
            default -> Player.PERMISSION_MEMBER;
        };
    }

    private static int migrateLegacyCommandPermission(String permission) {
        return switch (permission) {
            case "GAME_DIRECTORS", "OPERATOR" -> 1;
            case "ADMIN" -> 2;
            case "HOST" -> 3;
            case "OWNER" -> 4;
            case "INTERNAL", "AUTOMATION" -> 5;
            default -> 0;
        };
    }

    private static void migrateProgression(CompoundTag nbt) {
        if (!nbt.contains("PlayerGameMode")) {
            nbt.putInt("PlayerGameMode", Player.toStorageGamemode(nbt.getInt("playerGameType") & 0x03));
        }

        int level = Math.max(0, nbt.contains("PlayerLevel") ? nbt.getInt("PlayerLevel") : nbt.getInt("expLevel"));
        float progress = nbt.contains("PlayerLevelProgress")
                ? nbt.getFloat("PlayerLevelProgress")
                : Player.calculateExperienceProgress(nbt.getInt("EXP"), level);

        nbt.putInt("PlayerLevel", level);
        nbt.putFloat("PlayerLevelProgress", Math.max(0f, Math.min(1f, progress)));

        if (!nbt.contains("EnchantmentSeed")) {
            nbt.putInt("EnchantmentSeed", nbt.contains("enchSeed") ? nbt.getInt("enchSeed") : Player.generateEnchantmentSeed());
        }

        migrateExperienceAttributes(nbt, level, progress);

        nbt.remove("playerGameType", "EXP", "expLevel", "enchSeed");
    }

    private static void migrateExperienceAttributes(CompoundTag nbt, int level, float progress) {
        ListTag<CompoundTag> attributes = nbt.containsList("Attributes")
                ? nbt.getList("Attributes", CompoundTag.class)
                : new ListTag<>(Tag.TAG_Compound);

        float canonicalProgress = Math.max(0f, Math.min(1f, progress));

        ensureAttribute(attributes, Attribute.EXPERIENCE_LEVEL, level, true);
        ensureAttribute(attributes, Attribute.EXPERIENCE, canonicalProgress, true);

        for (CompoundTag attribute : attributes.getAll()) {
            switch (attribute.getString("Name")) {
                case "minecraft:player.level" -> {
                    attribute.putFloat("Base", level);
                    attribute.putFloat("Current", level);
                }
                case "minecraft:player.experience" -> {
                    attribute.putFloat("Base", canonicalProgress);
                    attribute.putFloat("Current", canonicalProgress);
                }
            }
        }

        nbt.putList("Attributes", attributes);
    }
}
