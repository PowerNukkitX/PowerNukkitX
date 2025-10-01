package cn.nukkit.item.customitem;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.data.CreativeCategory;
import cn.nukkit.item.customitem.data.CreativeGroup;
import cn.nukkit.item.customitem.data.DigProperty;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.item.utils.DiggerEntry;
import cn.nukkit.item.utils.ItemArmorType;
import cn.nukkit.item.utils.ItemEnchantSlot;
import cn.nukkit.item.utils.RepairEntry;
import cn.nukkit.item.utils.ShooterAmmo;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeCustomGroups;
import cn.nukkit.tags.ItemTags;
import cn.nukkit.utils.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.slf4j.Slf4j;


/**
 * CustomItemDefinition defines custom items from behavior packs.
 *
 * Use {@link CustomItemDefinition.SimpleBuilder} to declare all client-facing
 * properties and behaviors. The builder centralizes supported fields and
 * handles serialization automatically. <p>
 *
 * Override {@link Item Item} methods can be used for advanced or
 * specialized logic not covered by the builder.
 */
@Slf4j
public record CustomItemDefinition(String identifier, CompoundTag nbt) implements BlockID {
    private static final Object2IntOpenHashMap<String> INTERNAL_ALLOCATION_ID_MAP = new Object2IntOpenHashMap<>();
    private static final AtomicInteger nextRuntimeId = new AtomicInteger(10000);
    public record BlockPlacerData(String blockId, List<String> useOn) {}

    /**
     * Definition builder for custom items (deprecated signature).
     *
     * @param item deprecated ItemCustom
     * @deprecated Extend {@link Item} and implement {@link CustomItem}; use {@link #simpleBuilder(CustomItem)} instead.
     */
    @Deprecated
    public static CustomItemDefinition.SimpleBuilder simpleBuilder(ItemCustom item) {
        return new CustomItemDefinition.SimpleBuilder((Item) item);
    }

    /**
     * Definition builder for custom items (Item + CustomItem).
     *
     * @param item the custom item implementing {@link CustomItem}
     */
    public static <T extends Item & CustomItem> CustomItemDefinition.SimpleBuilder simpleBuilder(T item) {
        return new CustomItemDefinition.SimpleBuilder(item);
    }

    public static class SimpleBuilder {
        protected final String identifier;
        protected final CompoundTag nbt = new CompoundTag();
        private final Item item;
        protected String texture;
        protected String name;
        protected int maxStackSize = -1;
        protected List<String> tags;
        protected Float  miningSpeed;
        protected boolean shouldDespawn = true;
        protected Boolean stackedByData;
        protected String useAnimationType;
        protected Float   useModifierMovement;
        protected Float   useModifierDuration;
        protected Float cooldownDuration;
        protected String cooldownCategory;
        protected Integer maxDurability;
        protected Integer damageChanceMin;
        protected Integer damageChanceMax;
        protected Integer damage;
        protected Boolean  foodCanAlwaysEat;
        protected Integer  foodNutrition;
        protected Float    foodSaturation;
        protected String   foodUsingConvertsTo;
        protected ItemArmorType wearableSlot;
        protected Integer wearableProtection;
        protected Boolean wearableHidesPlayerLocation;
        private Boolean diggerUseEfficiency;



        protected SimpleBuilder(@NotNull Item customItem) {
            this.item = (Item) customItem;
            this.identifier = ((Item) customItem).getId();
        }

        private CompoundTag ensureComponents() {
            if (!nbt.contains("components")) {
                nbt.putCompound("components", new CompoundTag());
            }
            return nbt.getCompound("components");
        }

        protected CompoundTag ensureItemProperties() {
            CompoundTag components = ensureComponents();
            CompoundTag itemProps = components.getCompound("item_properties");
            if (!components.contains("item_properties")) {
                itemProps = new CompoundTag()
                        .putInt("creative_category", CreativeCategory.ITEMS.getId())
                        .putString("creative_group", "")
                        .putByte("is_hidden_in_commands", (byte) 0)
                        .putString("enchantable_slot", "none")
                        .putInt("enchantable_value", 0)
                        .putBoolean("allow_off_hand", false)
                        .putBoolean("can_destroy_in_creative", true)
                        .putInt("damage", 0)
                        .putBoolean("foil", false)
                        .putInt("frame_count", 1)
                        .putBoolean("hand_equipped", false)
                        .putBoolean("liquid_clipped", false)
                        .putInt("max_stack_size", 64)
                        .putFloat("mining_speed", 1.0f)
                        .putBoolean("should_despawn", true)
                        .putBoolean("stacked_by_data", false);
                components.putCompound("item_properties", itemProps);
            }
            return itemProps;
        }

        private CompoundTag ensureRepairable() {
            CompoundTag components = ensureComponents();
            if (!components.containsCompound("minecraft:repairable")) {
                components.putCompound("minecraft:repairable", new CompoundTag());
            }
            return components.getCompound("minecraft:repairable");
        }

        private ListTag<CompoundTag> ensureRepairItemsList() {
            CompoundTag repairable = ensureRepairable();
            ListTag<CompoundTag> list = repairable.getList("repair_items", CompoundTag.class);
            if (!repairable.containsList("repair_items")) {
                repairable.putList("repair_items", list);
            }
            return list;
        }

        private CompoundTag ensureDigger() {
            CompoundTag components = ensureComponents();
            if (!components.containsCompound("minecraft:digger")) {
                components.putCompound("minecraft:digger", new CompoundTag());
            }
            return components.getCompound("minecraft:digger");
        }

        private ListTag<CompoundTag> ensureDestroySpeeds() {
            CompoundTag digger = ensureDigger();
            ListTag<CompoundTag> list = digger.getList("destroy_speeds", CompoundTag.class);
            if (!digger.containsList("destroy_speeds")) {
                digger.putList("destroy_speeds", list);
            }
            return list;
        }

        private boolean hasTagApplied(String wanted) {
            if (wanted == null || wanted.isBlank()) return false;
            CompoundTag components = ensureComponents();

            if (components.containsList("item_tags")) {
                var list = components.getList("item_tags", StringTag.class);
                for (StringTag st : list.getAll()) if (wanted.equals(st.data)) return true;
            }

            if (components.containsCompound("minecraft:tags")) {
                CompoundTag mcTags = components.getCompound("minecraft:tags");
                if (mcTags.containsList("tags")) {
                    var list = mcTags.getList("tags", StringTag.class);
                    for (StringTag st : list.getAll()) if (wanted.equals(st.data)) return true;
                }
            }
            return false;
        }

        private void addTagIfAbsent(String tag) {
            if (tag == null || tag.isBlank() || hasTagApplied(tag)) return;

            CompoundTag components = ensureComponents();

            ListTag<StringTag> itemTags = components.getList("item_tags", StringTag.class);
            itemTags.add(new StringTag(tag));
            components.putList("item_tags", itemTags);

            CompoundTag mcTags = components.getCompound("minecraft:tags");
            ListTag<StringTag> tagsList = mcTags.getList("tags", StringTag.class);
            tagsList.add(new StringTag(tag));
            mcTags.putList("tags", tagsList);
            components.putCompound("minecraft:tags", mcTags);
        }

        private static String autoToolTagForSlot(String slotId) {
            if (slotId == null) return null;
            return switch (slotId) {
                case "pickaxe"  -> "minecraft:is_pickaxe";
                case "axe"      -> "minecraft:is_axe";
                case "shovel"   -> "minecraft:is_shovel";
                case "hoe"      -> "minecraft:is_hoe";
                case "sword"    -> "minecraft:is_sword";
                case "shears"   -> "minecraft:is_shears";
                default -> null;
            };
        }



        //////////////////////////
        // Builder Methods Start
        //////////////////////////

        /**
         * Sets the name of the item, if not defined will default to language resource_packs, <p>
         * if no translation is provided in RP, will default to the item Id.
         * @param name string item name
         */
        public SimpleBuilder name(String name) {
            Preconditions.checkArgument(!name.isBlank(), "name is blank");
            this.name = name;
            return this;
        }


        /**
         * The Creative Category that includes the specified item. Accepted categories are CONSTRUCTION | EQUIPMENT | ITEMS | NATURE | NONE <p>
         * Examples:
         * <pre>
         * // Specificy item to be at category tab Equipments
         * creativeCategory(CreativeCategory.EQUIPMENT);
         * </pre>
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
         */
        public SimpleBuilder creativeCategory(CreativeCategory creativeCategory) {
            ensureItemProperties().putInt("creative_category", creativeCategory.getId());
            return this;
        }

        /**
         * The Creative Group that that includes the specified item. Vanilla accepted groups can be defined by {@link CreativeGroup}. <p>
         * Examples:
         * <pre>
         * // Specificy the item to be part of chestplates group
         * creativeGroup(CreativeGroup.CHESTPLATE);
         * </pre>
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
         */
        public SimpleBuilder creativeGroup(CreativeGroup creativeGroup) {
            ensureItemProperties().putString("creative_group", creativeGroup.getGroupName());
            return this;
        }

        /**
         * The Creative Group that that includes the specified item. Custom groups can be defined by {@link CreativeCustomGroups}. <p>
         * Examples:
         * <pre>
         * // Specificy the item to be part of axes items
         * creativeGroup("minecraft:itemGroup.name.axe");
         * // Specificy the item to be part of a custom group (the group must be registered with {@link CreativeCustomGroups})
         * creativeGroup("My Custom Group");
         * </pre>
         * @see <a href="https://wiki.bedrock.dev/documentation/creative-categories.html#list-of-creative-categories">bedrock wiki</a>
         */
        public SimpleBuilder creativeGroup(String creativeGroup) {
            if (creativeGroup.isBlank()) {
                log.error("creativeGroup has an empty value!");
                return this;
            }
            ensureItemProperties().putString("creative_group", creativeGroup);
            return this;
        }

        /**
         * Sets if item will be hidden from commands and not able to be called from.
         * @param hidden boolean true/false
         */
        public SimpleBuilder isHiddenInCommands(boolean hidden) {
            ensureItemProperties().putByte("is_hidden_in_commands", hidden ? (byte) 1 : (byte) 0);
            return this;
        }

        /**
         * Sets the texture/icon for the item, this texture must be defined on Resource Pack/Client Side.
         * @param texture string name for the mapped texture in the resource_pack
         */
        public SimpleBuilder texture(String texture) {
            Preconditions.checkArgument(!texture.isBlank(), "texture name is blank");
            this.texture = texture;
            return this;
        }
        /**
         * Sets the texture/icon for the item, this texture must be defined on Resource Pack/Client Side.
         * @param iconTexture string name for the mapped texture in the resource_pack
         */
        public SimpleBuilder icon(String iconTexture) {
            return texture(iconTexture);
        }

        /**
         * Sets the item as a planter item component for blocks. <p>
         * Also allows the item to render as a 3D block, for that you need also to provide the desired block with your custom geometry.
         * @param blockId sting name id of the desired block
         * @param useOn set of string block Ids of blocks that this block can be placed
        */
        public SimpleBuilder blockPlacer(String blockId, String... useOn) {
            ListTag<CompoundTag> useOnList = new ListTag<>();
            if (useOn != null && useOn.length > 0) {
                for (String s : useOn) {
                    useOnList.add(new CompoundTag()
                        .putString("name", s)
                        .putCompound("states", new CompoundTag())
                        .putString("tags", ""));
                }
            }

            CompoundTag blockPlacer = new CompoundTag()
                .putString("block", blockId)
                .putBoolean("canUseBlockAsIcon", true)
                .putList("use_on", useOnList);

            CompoundTag components = ensureComponents();
            components.putCompound("minecraft:block_placer", blockPlacer);
            components.putCompound("minecraft:publisher_on_use_on",
                new CompoundTag().putBoolean("autoSucceedOnClient", false));
            return this;
        }

        /**
         * Sets the glint/foil effect of the item as it was enchanted (foil and glint are the same).
         * @param glint boolean true/false
         */
        public SimpleBuilder glint(boolean glint) {
            ensureItemProperties().putBoolean("foil", glint);
            ensureComponents().putCompound("minecraft:glint",
                    new CompoundTag().putByte("value", glint ? (byte) 1 : (byte) 0));
            return this;
        }
        /**
         * @deprecated Mojang have deprecated this component, prefer to use glint instead (foil and glint are the same).
         * @param foil boolean true/false
         */
        @Deprecated
        public SimpleBuilder foil(boolean foil) {
            return glint(foil);
        }

        /**
         * The allow_off_hand component determines whether the item can be placed in the off hand slot of the inventory.
         * @param allowOffHand boolean true/false
         */
        public SimpleBuilder allowOffHand(boolean allowOffHand) {
            CompoundTag itemProps = ensureItemProperties();
            itemProps.putBoolean("allow_off_hand", allowOffHand);
            ensureComponents().putCompound("minecraft:allow_off_hand",
                    new CompoundTag().putByte("value", allowOffHand ? (byte) 1 : (byte) 0));

            return this;
        }

        /**
         * The hand_equipped component determines if an item is rendered like a tool while it is in a player's hand.
         * @param handEquipped boolean true/false
         */
        public SimpleBuilder handEquipped(boolean handEquipped) {
            ensureItemProperties().putBoolean("hand_equipped", handEquipped);
            return this;
        }

        /**
         * The can_destroy_in_creative component determines if the item can be used by a player to break blocks when in creative mode.
         * @param value boolean true/flase
         */
        public SimpleBuilder canDestroyInCreative(boolean value) {
            ensureItemProperties().putBoolean("can_destroy_in_creative", value);
            return this;
        }

        /**
         * Make persistent component determines if the item should eventually despawn/or not while floating in the world
         * @param persistent boolean true/flase
         */
        public SimpleBuilder shouldDespawn(boolean shouldDespawn) {
            this.shouldDespawn = shouldDespawn;
            return this;
        }

        /**
         * The stacked_by_data component determines whether the same items with different aux values can stack.
         * @param stacked boolean true/flase
         */
        public SimpleBuilder stackedByData(boolean stacked) {
            this.stackedByData = stacked;
            return this;
        }

        /**
         * Determines which tags are included on a given item.
         * <pre>
         * // Single tag
         * tag("my_custom_tag");
         * // Multiple tags
         * tag("my_custom_tag1", "my_custom_tag2");
         * </pre>
         * @param tags set of string tags
         */
        public SimpleBuilder tag(String... tags) {
            if (tags == null || tags.length == 0) return this;

            LinkedHashSet<String> unique = new LinkedHashSet<>();
            for (String t : tags) {
                if (t == null || t.isBlank()) continue;
                Identifier.assertValid(t);
                unique.add(t);
            }
            if (unique.isEmpty()) return this;
            this.tags = new ArrayList<>(unique);

            var components = ensureComponents();

            var itemTags = new ListTag<StringTag>();
            for (String t : unique) itemTags.add(new StringTag(t));

            components.putList("item_tags", itemTags);
            components.putCompound("minecraft:tags", new CompoundTag().putList("tags", itemTags));

            return this;
        }

        /**
         * Determines how many of an item can be stacked together.
         * @param size int value
         */
        public SimpleBuilder maxStackSize(int size) {
            this.maxStackSize = size;
            return this;
        }

        /**
         * Sets the current item damage/attack power value.
         * @param damage int value, must be >= 0.
         */
        public SimpleBuilder damage(int damage) {
            Preconditions.checkArgument(damage >= 0, "damage must be >= 0");
            this.damage = damage;
            return this;
        }

        /**
         * Sets item durability (Bedrock "minecraft:durability"). <p>
         * If not set: damage_chance min & max defaults to 100.
         * @param maxDurability int value, must be >= 0
         */
        public SimpleBuilder durability(int maxDurability) {
            Preconditions.checkArgument(maxDurability > 0, "maxDurability must be > 0");
            return durability(maxDurability, 100, 100);
        }

        /**
         * Sets item durability (Bedrock "minecraft:durability"). <p>
         * damage_chance will randomize between the min & max.
         * @param maxDurability int value, must be >= 0.
         * @param damageChanceMin int value, percentage change must be >= 0 & <=100.
         * @param damageChanceMax int value, percentage change must be >= 0 & <=100.
         */
        public SimpleBuilder durability(int maxDurability, int damageChanceMin, int damageChanceMax) {
            Preconditions.checkArgument(maxDurability >= 0, "maxDurability must be >= 0");
            Preconditions.checkArgument(damageChanceMin >= 0 && damageChanceMin <= 100, "maxDurability must be >= 0 & <=100");
            Preconditions.checkArgument(damageChanceMax >= 0 && damageChanceMax <= 100, "maxDurability must be >= 0 & <=100");
            this.maxDurability = maxDurability;
            this.damageChanceMin = Math.max(0, damageChanceMin);
            this.damageChanceMax = Math.max(0, damageChanceMax);
            return this;
        }

        /**
         * Determines what enchantments can be applied to the item.
         * <pre>
         * // Using ItemEchantSlot
         * builder.enchantable(ItemEnchantSlot.SWORD, 10);
         * // Using String slot
         * builder.enchantable("pickaxe", 20);
         * </pre>
         * @param slot {@link ItemEnchantSlot} slot ID of the enchantable item
         * @param value int value, must be >= 0
         */
        public SimpleBuilder enchantable(ItemEnchantSlot slot, int value) {
            if (value < 0) value = 0;
            if (slot == ItemEnchantSlot.NONE) return this;
            return this.enchantable(slot.id(), value);
        }

        /**
         * Determines what enchantments can be applied to the item.
         * <pre>
         * // Using ItemEchantSlot
         * builder.enchantable(ItemEnchantSlot.SWORD, 10);
         * // Using String slot
         * builder.enchantable("pickaxe", 20);
         * </pre>
         * @param slot string slot ID of the enchantable item
         * @param value int value, must be >= 0
         */
        public SimpleBuilder enchantable(String slot, int value) {
            if (slot == null || slot.isBlank()) return this;
            if (value <= 0) return this;
            if (value > 255) value = 255;

            CompoundTag itemProps = ensureItemProperties();
            itemProps.putString("enchantable_slot", slot);
            itemProps.putInt("enchantable_value", value);

            ensureComponents().putCompound("minecraft:enchantable",
                    new CompoundTag()
                            .putString("slot", slot)
                            .putByte("value", (byte) value));

            return this;
        }

        /**
         * Add one or more {@code destroy_speeds} entries to {@code minecraft:digger}. <p>
         * Each entry defines a mining speed and the blocks it applies to (either by a block id, a tag expression, or both).
         *
         * Examples:
         * <pre>
         * // Specific block only
         * digger(DiggerEntry.block("minecraft:coal_ore", 2));
         * // built-in tag group (all wooden blocks)
         * digger(DiggerEntry.create().speed(6).addAllWooden());
         * // Custom tag
         * digger(DiggerEntry.create().speed(5).addTags("'my_custom_tag'"));
         * // Mix and match
         * digger(
         *     DiggerEntry.create().speed(6).addTags("'wood'", "'my_custom_tag'"),
         *     DiggerEntry.block("minecraft:coal_ore", 2)
         * ).useEfficiency(true);
         * </pre>
         * @param diggerEntries {@link DiggerEntry} of entries that can be used as single block or tags for query expressions.
         */
        public SimpleBuilder digger(DiggerEntry... diggerEntries) {
            if (diggerEntries == null || diggerEntries.length == 0) return this;

            var list = ensureDestroySpeeds();
            for (DiggerEntry e : diggerEntries) {
                if (e != null) list.add(e.toNbt());
            }

            if (this.diggerUseEfficiency != null) {
                ensureDigger().putBoolean("use_efficiency", this.diggerUseEfficiency);
            }
            return this;
        }
        /**
         * Sets the {@code use_efficiency} flag for {@code minecraft:digger}. <p>
         * This value is only written when a {@link #digger(DiggerEntry...)} call. <p>
         * Example:
         * <pre>
         * digger(
         *     DiggerEntry.block("minecraft:coal_ore", 2),
         *     DiggerEntry.create().speed(6).addAllWooden()
         * ).useEfficiency(true);
         * </pre>
         *
         * @param useEfficiency whether efficiency enchantments should speed up mining
         */
        public SimpleBuilder useEfficiency(boolean useEfficiency) {
            this.diggerUseEfficiency = useEfficiency;
            return this;
        }

        /**
         * Set Armor Item. <p>
         * Note: Bedrock requires the max stack size is set to 1. <p>
         * protection defaults to 0, hides_player_location defaults to false.
         * Examples:
         * <pre>
         * // Set wearable with string slot ID
         * wearable("armor_head");
         * </pre>
         * @param slot string slot where the item will be allowed to wear
         * @throws IllegalArgumentException if slot is unknown
         */
        public SimpleBuilder wearable(String slot) {
            return wearable(slot, 0, false);
        }

        /**
         * Set Armor Item. <p>
         * Note: Bedrock requires the max stack size is set to 1. <p>
         * hides_player_location defaults to false.
         * Examples:
         * <pre>
         * // Set wearable with string slot ID and protection value
         * wearable("armor_head", 7);
         * </pre>
         * @param slot string slot where the item will be allowed to wear
         * @param protection protection level of the item.
         * @throws IllegalArgumentException if slot is unknown
         */
        public SimpleBuilder wearable(String slot, int protection) {
            return wearable(slot, protection, false);
        }

        /**
         * Set Armor Item. <p>
         * Note: Bedrock requires the max stack size is set to 1. <p>
         * hides_player_location defaults to false.
         * Examples:
         * <pre>
         * // Set wearable with string slot ID, protection value and hides_player_location true
         * wearable("armor_head", 7, true);
         * </pre>
         * @param slot string slot where the item will be allowed to wear
         * @param protection protection level of the item.
         * @param hidesPlayerLocation if true and worn hides player on locator maps and the locator bar.
         * @throws IllegalArgumentException if slot is unknown
         */
        public SimpleBuilder wearable(String slot, int protection, boolean hidesPlayerLocation) {
            ItemArmorType typeSlot = ItemArmorType.get(slot);
            Preconditions.checkArgument(typeSlot != ItemArmorType.NONE, "Unknown wearable slot: %s", slot);
            return wearable(typeSlot, protection, hidesPlayerLocation);
        }

        /**
         * Set Armor Item. <p>
         * Note: Bedrock requires the max stack size is set to 1. <p>
         * protection defaults to 0, hides_player_location defaults to false.
         * Examples:
         * <pre>
         * // Set wearable with armor type ID
         * wearable(ItemArmorType.HEAD);
         * </pre>
         * @param slot ItemArmorType where the item will be allowed to wear
         */
        public SimpleBuilder wearable(ItemArmorType slot) {
            return wearable(slot, 0, false);
        }

        /**
         * Set Armor Item. <p>
         * Note: Bedrock requires the max stack size is set to 1. <p>
         * hides_player_location defaults to false.
         * Examples:
         * <pre>
         * // Set wearable with armor type ID and protection value
         * wearable(ItemArmorType.HEAD, 7);
         * </pre>
         * @param slot ItemArmorType where the item will be allowed to wear
         * @param protection protection level of the item.
         */
        public SimpleBuilder wearable(ItemArmorType slot, int protection) {
            return wearable(slot, protection, false);
        }

        /**
         * Set Armor Item. <p>
         * Note: Bedrock requires the max stack size is set to 1. <p>
         * hides_player_location defaults to false.
         * Examples:
         * <pre>
         * // Set wearable with armor type ID, protection value and hides_player_location true
         * wearable(ItemArmorType.HEAD, 7, true);
         * </pre>
         * @param slot ItemArmorType where the item will be allowed to wear
         * @param protection protection level of the item.
         * @param hidesPlayerLocation if true and worn hides player on locator maps and the locator bar.
         */
        public SimpleBuilder wearable(ItemArmorType slot, int protection, boolean hidesPlayerLocation) {
            Preconditions.checkArgument(slot != ItemArmorType.NONE, "wearable slot cannot be null");
            Preconditions.checkArgument(protection >= 0, "protection must be >= 0");
            this.wearableSlot = slot;
            this.wearableProtection = protection;
            this.wearableHidesPlayerLocation = hidesPlayerLocation;
            return this;
        }

        /**
         * Defines the items that can be used to repair a defined item, and the amount of durability each item restores upon repair.
         * Examples:
         * <pre>
         * // single entry with fixed durability restored
         * repairable(RepairEntry.set("minecraft:gold_ingot").amount(80f));
         * // multiple repairEntries at once
         * repairable(
         *     RepairEntry.set("minecraft:iron_ingot")
         *         .amountExpr("context.other->q.remaining_durability + 0.25 * context.other->q.max_durability"),
         *     RepairEntry.set("minecraft:gold_ingot", "awp:my_custom_item")
         *         .amount(80f)
         * );
         * </pre>
         * @param repairEntries one or more {@link RepairEntry} objects
         */
        public SimpleBuilder repairable(RepairEntry... repairEntries) {
            if (repairEntries == null || repairEntries.length == 0) return this;
            var list = ensureRepairItemsList();
            for (RepairEntry e : repairEntries) {
                if (e != null) list.add(e.toNbt());
            }
            return this;
        }

        public SimpleBuilder repairable(float amount, String... itemIds) {
            return repairable(RepairEntry.set(itemIds).amount(amount));
        }

        public SimpleBuilder repairable(String expression, String... itemIds) {
            if (expression == null || expression.isBlank()) return this;
            return repairable(RepairEntry.set(itemIds).amountExpr(expression));
        }

        public SimpleBuilder repairable(float amount, Item... items) {
            return repairable(RepairEntry.set(items).amount(amount));
        }

        public SimpleBuilder repairable(String expression, Item... items) {
            if (expression == null || expression.isBlank()) return this;
            return repairable(RepairEntry.set(items).amountExpr(expression));
        }

        public SimpleBuilder repairable(List<String> itemIds, float amount) {
            return repairable(RepairEntry.set(itemIds != null ? itemIds.toArray(new String[0]) : new String[0]).amount(amount));
        }

        public SimpleBuilder repairable(List<String> itemIds, String expression) {
            if (expression == null || expression.isBlank()) return this;
            return repairable(RepairEntry.set(itemIds != null ? itemIds.toArray(new String[0]) : new String[0]).amountExpr(expression));
        }

        /**
         * Sets the item as a food component, allowing it to be edible to the player. <p>
         * Note: Bedrock requires {@code minecraft:use_modifiers} for eat/drink behavior.
         * Call {@link #useModifiers(float, float)} to set the use time (seconds) and movement modifier.
         * If you skip it, this builder will use duration as 0s and movement as 1.0 by default. <p>
         * 
         * nutrition defaults to 0, saturation_modifier defaults to 0.6, using_converts_to defaults to "".
         * 
         * @param canAlwaysEat if true, food can be always eaten even without hungry
         */
        public SimpleBuilder food(boolean canAlwaysEat) {
            return food(canAlwaysEat, 0, 0.6f);
        }

        /**
         * Sets the item as a food component, allowing it to be edible to the player. <p>
         * Note: Bedrock requires {@code minecraft:use_modifiers} for eat/drink behavior.
         * Call {@link #useModifiers(float, float)} to set the use time (seconds) and movement modifier.
         * If you skip it, this builder will use duration as 0s and movement as 1.0 by default. <p>
         * 
         * using_converts_to defaults to "".
         * 
         * @param canAlwaysEat if true, food can be always eaten even without hungry
         * @param nutrition nutrition level of the food as int value
         * @param saturationModifier saturation modifier provides the saturation of the food by: (nutrition * saturation_modifier * 2), defaults is 0.6
         */
        public SimpleBuilder food(boolean canAlwaysEat, int nutrition, float saturationModifier) {
            return food(canAlwaysEat, nutrition, saturationModifier, "");
        }

        /**
         * Sets the item as a food component, allowing it to be edible to the player. <p>
         * Note: Bedrock requires {@code minecraft:use_modifiers} for eat/drink behavior.
         * Call {@link #useModifiers(float, float)} to set the use time (seconds) and movement modifier.
         * If you skip it, this builder will use duration as 0s and movement as 1.0 by default.
         * 
         * @param canAlwaysEat if true, food can be always eaten even without hungry
         * @param nutrition nutrition level of the food as int value
         * @param saturationModifier saturation modifier provides the saturation of the food by: (nutrition * saturation_modifier * 2), defaults is 0.6
         * @param usingConvertsTo string item ID that this food will convert to after eaten, example "minecraft:bowl"
         */
        public SimpleBuilder food(boolean canAlwaysEat, int nutrition, float saturationModifier, String usingConvertsTo) {
            Preconditions.checkArgument(nutrition >= 0, "nutrition must be >= 0");
            Preconditions.checkArgument(saturationModifier >= 0f, "saturation_modifier must be >= 0");

            this.foodCanAlwaysEat = canAlwaysEat;
            this.foodNutrition = nutrition;
            this.foodSaturation = saturationModifier;
            this.foodUsingConvertsTo = (usingConvertsTo == null) ? "" : usingConvertsTo;
            return this;
        }

        /**
         * Compels an item to shoot projectiles, similarly to a bow or crossbow. <p>
         * Supports optional ammunition definitions ({@link ShooterAmmo}),
         * Example:
         * <pre>
         * customBuilder(myBow)
         *     .shooter(1.0f,
         *         ShooterAmmo.set("awp:arrow")
         *             .useOffhand(true)
         *             .searchInventory(true)
         *             .useInCreative(true)
         *     );
         * </pre>
         * @param maxDrawDuration          Time (in seconds) to fully draw the shooter
         * @param scalePowerByDrawDuration If true, power scales with draw time
         * @param chargeOnDraw             If true, consumes ammo immediately on draw
         * @param ammunition               Optional list of allowed ammunition types
         */
        public SimpleBuilder shooter(float maxDrawDuration,
                                     boolean scalePowerByDrawDuration,
                                     boolean chargeOnDraw,
                                     ShooterAmmo... ammunition) {

            var shooter = new CompoundTag()
                    .putFloat("max_draw_duration", maxDrawDuration)
                    .putByte("scale_power_by_draw_duration", (byte) (scalePowerByDrawDuration ? 1 : 0))
                    .putByte("charge_on_draw", (byte) (chargeOnDraw ? 1 : 0));

            if (ammunition != null && ammunition.length > 0) {
                var ammoList = new ListTag<CompoundTag>();
                for (ShooterAmmo a : ammunition) {
                    if (a != null) ammoList.add(a.toNbt());
                }
                if (!ammoList.getAll().isEmpty()) shooter.putList("ammunition", ammoList);
            }

            ensureComponents().putCompound("minecraft:shooter", shooter);
            return this;
        }

        /**
         * Compels an item to shoot projectiles, similarly to a bow or crossbow. <p>
         * Defaults: scalePowerByDrawDuration = true, chargeOnDraw = false.
         * Example:
         * <pre>
         * customBuilder(myBow)
         *     .shooter(1.2f, ShooterAmmo.set("awp:arrow"));
         * </pre>
         */
        public SimpleBuilder shooter(float maxDrawDuration, ShooterAmmo... ammunition) {
            return shooter(maxDrawDuration, true, false, ammunition);
        }

        /**
         * Compels an item to shoot projectiles, similarly to a bow or crossbow. <p>
         * Example:
         * <pre>
         * customBuilder(myWand)
         *     .shooterFlags(0.5f, true, false);
         * </pre>
         */
        public SimpleBuilder shooterFlags(float maxDrawDuration,
                                          boolean scalePowerByDrawDuration,
                                          boolean chargeOnDraw) {
            return shooter(maxDrawDuration, scalePowerByDrawDuration, chargeOnDraw);
        }

        /**
         * Sets the throwable item component. Configures the {@code minecraft:throwable} component. <p>
         * @param doSwingAnimation Determines whether the item should use the swing animation when thrown.
         * @param launchPowerScale The scale at which the power of the throw increases.
         * @param maxDrawDuration The maximum duration to draw a throwable item.
         * @param maxLaunchPower The maximum power to launch the throwable item.
         * @param minDrawDuration The minimum duration to draw a throwable item.
         * @param scalePowerByDrawDuration Whether or not the power of the throw increases with duration charged.
         */
        public SimpleBuilder throwable(boolean doSwingAnimation,
                                       float launchPowerScale,
                                       float maxDrawDuration,
                                       float maxLaunchPower,
                                       float minDrawDuration,
                                       boolean scalePowerByDrawDuration) {

            Preconditions.checkArgument(launchPowerScale >= 0f, "launch_power_scale must be >= 0");
            Preconditions.checkArgument(maxLaunchPower   >= 0f, "max_launch_power must be >= 0");
            Preconditions.checkArgument(maxDrawDuration  >= 0f, "max_draw_duration must be >= 0");
            Preconditions.checkArgument(minDrawDuration  >= 0f, "min_draw_duration must be >= 0");

            ensureComponents().putCompound("minecraft:throwable", new CompoundTag()
                .putByte ("do_swing_animation",          (byte) (doSwingAnimation ? 1 : 0))
                .putFloat("launch_power_scale",          launchPowerScale)
                .putFloat("max_draw_duration",           maxDrawDuration)
                .putFloat("max_launch_power",            maxLaunchPower)
                .putFloat("min_draw_duration",           minDrawDuration)
                .putByte ("scale_power_by_draw_duration",(byte) (scalePowerByDrawDuration ? 1 : 0))
            );

            return this;
        }

        /**
         * Sets the throwable item component. Configures the {@code minecraft:throwable} component. <p>
         * @param doSwingAnimation Determines whether the item should use the swing animation when thrown.
         * @param launchPowerScale The scale at which the power of the throw increases.
         * @param maxLaunchPower The maximum power to launch the throwable item.
         * @param maxDrawDuration The maximum duration to draw a throwable item. Default is set to 0.0.
         * @param minDrawDuration The minimum duration to draw a throwable item. Default is set to 0.0.
         * @param scalePowerByDrawDuration Whether or not the power of the throw increases with duration charged. Default is set to false
         */
        public SimpleBuilder throwable(boolean doSwingAnimation,
                                       float launchPowerScale,
                                       float maxLaunchPower) {
            return throwable(doSwingAnimation, launchPowerScale, 0f, maxLaunchPower, 0f, false);
        }

        /**
         * Allows this item to be used as fuel in a furnace to 'cook' other items.
         * @param duration float seconds this item burns for.
         */
        public SimpleBuilder fuel(float duration) {
            Preconditions.checkArgument(duration > 0, "Fuel duration must be > 0");

            ensureComponents().putCompound("minecraft:fuel",
                    new CompoundTag().putFloat("duration", duration));

            return this;
        }

        /**
         * use_animation specifies which animation is played when the player uses the item.
         * Examples: "eat", "drink", "bow".
         * @param animation string animation
         */
        public SimpleBuilder useAnimation(String animation) {
            Preconditions.checkArgument(animation != null && !animation.isBlank(), "useAnimation cannot be blank");
            this.useAnimationType = animation;
            return this;
        }

        /**
         * Determines how long an item takes to use in combination with components such as Shooter, Throwable, or Food.
         * @param movementModifier float moviment reduction while using this item
         * @param useDurationSeconds float seconds to successful use this item
        */
        public SimpleBuilder useModifiers(float movementModifier, float useDurationSeconds) {
            Preconditions.checkArgument(movementModifier > 0f && movementModifier <= 1f, "movementModifier must be in (0,1]");
            Preconditions.checkArgument(useDurationSeconds >= 0f, "useDurationSeconds must be >= 0");
            this.useModifierMovement = movementModifier;
            this.useModifierDuration = useDurationSeconds;
            return this;
        }

        /**
         * The duration of time (in seconds) items with a matching category will spend cooling down before becoming usable again.
         * @param category string category cooldown name of this item
         * @param duration float seconds value to fully cooldown items of the same category name
        */
        public SimpleBuilder cooldown(String category, float duration) {
            this.cooldownCategory = category;
            this.cooldownDuration = duration;
            return this;
        }


        //////////////////////////
        // Builder Methods End
        //////////////////////////



        /**
         * Custom processing of the item to be sent to the client ComponentNBT, which contains all definitions for custom item.<p>
         * You can modify them as much as you want, under the right conditions.
         */
        public CustomItemDefinition customBuild(Consumer<CompoundTag> nbtConsumer) {
            var def = this.build();
            nbtConsumer.accept(def.nbt);
            return def;
        }

        public CustomItemDefinition build() {
            return buildDefinition();
        }

        /**
         * Finalizes and assembles the complete custom item definition NBT to be sent to the client.<p>
        */
        protected CustomItemDefinition buildDefinition() {
            CompoundTag itemProps  = ensureItemProperties();
            CompoundTag components = ensureComponents();

            writeIcon(itemProps);
            writeDisplayName(components);
            writeStackSize(itemProps, components);
            writeBaseItemProps(itemProps);
            writeUseAnimationAndModifiers(itemProps, components);
            writeCooldown(components);

            CustomItemDefinition result = new CustomItemDefinition(identifier, nbt);
            allocateRuntimeId(result.identifier());

            writeDurability(components);
            writeDamage(itemProps, components);
            writeFood(components, itemProps);
            writeWearable(components);
            applyAutoToolTag(itemProps);

            return result;
        }

        // Write Build Definition Helpers
        private void writeIcon(CompoundTag itemProps) {
            if (texture != null && !texture.isBlank()) {
                itemProps.putCompound("minecraft:icon",
                        new CompoundTag().putCompound("textures",
                                new CompoundTag().putString("default", texture)));
            }
        }

        private void writeDisplayName(CompoundTag components) {
            if (name != null) {
                components.putCompound("minecraft:display_name",
                        new CompoundTag().putString("value", name));
            }
        }

        private void writeStackSize(CompoundTag itemProps, CompoundTag components) {
            int stackSize = maxStackSize > 0 ? maxStackSize : item.getMaxStackSize();
            if (this.wearableSlot != null) stackSize = 1;
            itemProps.putInt("max_stack_size", stackSize);
            components.putCompound("minecraft:max_stack_size",
                    new CompoundTag().putByte("value", (byte) stackSize));
        }

        private void writeBaseItemProps(CompoundTag itemProps) {
            itemProps.putFloat("mining_speed", this.miningSpeed != null ? this.miningSpeed : 1.0f);
            itemProps.putBoolean("should_despawn", shouldDespawn);
            itemProps.putBoolean("stacked_by_data", this.stackedByData != null ? this.stackedByData : false);
        }

        private void writeUseAnimationAndModifiers(CompoundTag itemProps, CompoundTag components) {
            int animationId = resolveAnimationId(this.useAnimationType);
            itemProps.putInt("use_animation", animationId);

            if (this.useAnimationType != null) {
                components.putCompound("minecraft:use_animation",
                        new CompoundTag().putString("value", this.useAnimationType));
            }

            int useDurationTicks = toTicks(this.useModifierDuration);
            itemProps.putInt("use_duration", useDurationTicks);

            if (this.useModifierMovement != null && this.useModifierDuration != null) {
                components.putCompound("minecraft:use_modifiers",
                        new CompoundTag()
                                .putFloat("movement_modifier", this.useModifierMovement)
                                .putFloat("use_duration", this.useModifierDuration));
            }
        }

        private void writeCooldown(CompoundTag components) {
            if (cooldownCategory != null && cooldownDuration != null) {
                components.putCompound("minecraft:cooldown", new CompoundTag()
                        .putString("category", cooldownCategory)
                        .putFloat("duration", cooldownDuration));
            }
        }

        private void allocateRuntimeId(String idStr) {
            int id;
            if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(idStr)) {
                while (INTERNAL_ALLOCATION_ID_MAP.containsValue(id = nextRuntimeId.getAndIncrement())) {}
                INTERNAL_ALLOCATION_ID_MAP.put(idStr, id);
            }
        }

        private void writeDurability(CompoundTag components) {
            if (this.maxDurability == null) return;

            CompoundTag durability = new CompoundTag().putInt("max_durability", this.maxDurability);
            if (this.damageChanceMin != null && this.damageChanceMax != null) {
                durability.putCompound("damage_chance",
                        new CompoundTag()
                                .putInt("min", this.damageChanceMin)
                                .putInt("max", this.damageChanceMax));
            }
            components.putCompound("minecraft:durability", durability);
        }

        private void writeDamage(CompoundTag itemProps, CompoundTag components) {
            if (this.damage == null) return;

            itemProps.putInt("damage", this.damage);
            components.putCompound("minecraft:damage",
                    new CompoundTag().putByte("value", this.damage.intValue() & 0xFF));
        }

        private void writeFood(CompoundTag components, CompoundTag itemProps) {
            boolean hasFood =
                    (foodCanAlwaysEat != null) ||
                    (foodNutrition != null) ||
                    (foodSaturation != null) ||
                    (foodUsingConvertsTo != null);

            if (!hasFood) return;

            CompoundTag food = new CompoundTag();
            if (foodCanAlwaysEat != null) food.putBoolean("can_always_eat", foodCanAlwaysEat);
            if (foodNutrition != null) food.putInt("nutrition", foodNutrition);
            if (foodSaturation != null) food.putFloat("saturation_modifier", foodSaturation);
            if (foodUsingConvertsTo != null) food.putString("using_converts_to", foodUsingConvertsTo);
            components.putCompound("minecraft:food", food);

            // Default use_modifiers for food if none provided
            if (this.useModifierMovement == null && this.useModifierDuration == null) {
                itemProps.putInt("use_duration", 0);
                components.putCompound("minecraft:use_modifiers",
                        new CompoundTag()
                                .putFloat("movement_modifier", 1.0f)
                                .putFloat("use_duration", 0.0f));
            }
        }

        private void writeWearable(CompoundTag components) {
            if (this.wearableSlot == null) return;

            CompoundTag wearable = new CompoundTag()
                    .putString("slot", this.wearableSlot.id())
                    .putInt("protection", this.wearableProtection != null ? this.wearableProtection : 0)
                    .putBoolean("hides_player_location",
                            this.wearableHidesPlayerLocation != null && this.wearableHidesPlayerLocation);
            components.putCompound("minecraft:wearable", wearable);
        }

        private void applyAutoToolTag(CompoundTag itemProps) {
            String slotIdForTag = itemProps.getString("enchantable_slot");
            String autoTag = autoToolTagForSlot(slotIdForTag);
            if (autoTag != null) addTagIfAbsent(autoTag);
        }

        private static int resolveAnimationId(String type) {
            if (type == null) return 0;
            return switch (type.toLowerCase(Locale.ROOT)) {
                case "eat"    -> 1;
                case "drink"  -> 2;
                case "bow"    -> 4;
                default       -> 0; // unknown/none
            };
        }

        private static int toTicks(Float seconds) {
            return (seconds == null) ? 0 : Math.max(0, Math.round(seconds * 20f));
        }




        //////////////////////////////////
        // Deprecated legacy methods
        //////////////////////////////////

        /**
         * @deprecated Legacy method for adding repair rules.
         * <p>
         * This method writes {@code minecraft:repairable.repair_items} directly
         * with a list of item names and a Molang expression for the repair amount.
         * It only supports expression-based repair.
         * <p>
         * Use {@link #repairable(RepairEntry...)} instead.
         * <ul>
         *   <li>Support both numeric and expression-based repair amounts</li>
         *   <li>Allow flexible argument styles (IDs, Items, Lists)</li>
         * </ul>
         */
        @Deprecated
        protected SimpleBuilder addRepairs(@NotNull List<String> repairItemNames, String molang) {
            if (molang.isBlank()) {
                log.error("repairAmount has an invalid value!");
                return this;
            }

            CompoundTag components = nbt.getCompound("components");
            CompoundTag repairable = components.getCompound("minecraft:repairable");

            if (!components.contains("minecraft:repairable")) {
                repairable = new CompoundTag();
                components.putCompound("minecraft:repairable", repairable);
            }

            ListTag<CompoundTag> repairItems = repairable.getList("repair_items", CompoundTag.class);
            if (repairItems == null) repairItems = new ListTag<>();

            ListTag<CompoundTag> items = new ListTag<>();
            for (String name : repairItemNames) {
                items.add(new CompoundTag().putString("name", name));
            }

            repairItems.add(new CompoundTag()
                    .putList("items", items)
                    .putCompound("repair_amount", new CompoundTag()
                            .putString("expression", molang)
                            .putInt("version", 1)));

            repairable.putList("repair_items", repairItems);
            return this;
        }

        /**
         * @deprecated This method was deprecated by Mojang and can suddenly stop working on Minecraft clients, prefer migrating your items to use resource_packs attachables.
         * Control rendering offsets of custom items at different viewpoints
         */
        @Deprecated
        public SimpleBuilder renderOffsets(@NotNull RenderOffsets renderOffsets) {
            CompoundTag components = nbt.getCompound("components");
            if (!nbt.contains("components")) {
                components = new CompoundTag();
                nbt.putCompound("components", components);
            }
            components.putCompound("minecraft:render_offsets", renderOffsets.nbt);
            return this;
        }

        /**
         * @deprecated Mining speed is not present in any Microsoft / Mojang docs,
         * it might have deprecated as it also is part of item properties as most of
         * legacy deprecated components.
         *
         * Prefer using {@link digger} component.
         */
        @Deprecated
        public SimpleBuilder miningSpeed(float speed) {
            Preconditions.checkArgument(speed >= 0f, "miningSpeed must be >= 0");
            this.miningSpeed = speed;
            return this;
        }

        /**
         * @deprecated to avoid misleading, use {@link #shouldDespawn()} instead.
         * Make persistent component determines if the item should eventually despawn/or not while floating in the world
         * @param persistent boolean true/flase
         */
        @Deprecated
        public SimpleBuilder makePersistent(boolean makePersistent) {
            return shouldDespawn(!makePersistent);
        }
    }

    /**
     * Definition builder for custom items
     *
     * @param item the item
     * @return the custom item definition . simple builder
     */
    public static <T extends Item & CustomItem> CustomItemDefinition.SimpleBuilder customBuilder(T item) {
        return simpleBuilder(item);
    }



    ////////////////////////
    // Deprecated builders
    ////////////////////////

    /**
     * Definition builder for custom tools
     *
     * @param item the item
     */
    @Deprecated
    public static CustomItemDefinition.ToolBuilder toolBuilder(ItemCustomTool item) {
        return new CustomItemDefinition.ToolBuilder(item);
    }

    @Deprecated
    public static class ToolBuilder extends SimpleBuilder {
        private final ItemCustomTool item;
        private final List<CompoundTag> blocks = new ArrayList<>();
        private final List<String> blockTags = new ArrayList<>();
        private final CompoundTag diggerRoot = new CompoundTag()
                .putBoolean("use_efficiency", true)
                .putList("destroy_speeds", new ListTag<>(Tag.TAG_Compound));
        private Integer speed = null;

        public static Map<String, Map<String, DigProperty>> toolBlocks = new HashMap<>();

        static {
            var pickaxeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var axeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var shovelBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var hoeBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            var swordBlocks = new Object2ObjectOpenHashMap<String, DigProperty>();
            for (var name : List.of(ACACIA_SLAB, BAMBOO_MOSAIC_SLAB, BAMBOO_SLAB, BIRCH_SLAB, BLACKSTONE_SLAB, BRICK_SLAB, CHERRY_SLAB, COBBLED_DEEPSLATE_SLAB, COBBLESTONE_SLAB, CRIMSON_SLAB, CUT_COPPER_SLAB, DARK_OAK_SLAB, DEEPSLATE_BRICK_SLAB, DEEPSLATE_TILE_SLAB, EXPOSED_CUT_COPPER_SLAB, JUNGLE_SLAB, MANGROVE_SLAB, MUD_BRICK_SLAB, NETHER_BRICK_SLAB, OAK_SLAB, OXIDIZED_CUT_COPPER_SLAB, PETRIFIED_OAK_SLAB, POLISHED_BLACKSTONE_BRICK_SLAB, POLISHED_BLACKSTONE_SLAB, POLISHED_DEEPSLATE_SLAB, POLISHED_TUFF_SLAB, QUARTZ_SLAB, SANDSTONE_SLAB, SMOOTH_STONE_SLAB, SPRUCE_SLAB, STONE_BRICK_SLAB, TUFF_BRICK_SLAB, TUFF_SLAB, WARPED_SLAB, WAXED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_SLAB,
                    ICE, BLUE_ICE, UNDYED_SHULKER_BOX, BLUE_SHULKER_BOX, RED_SHULKER_BOX, BLACK_SHULKER_BOX, CYAN_SHULKER_BOX, BROWN_SHULKER_BOX, LIME_SHULKER_BOX, GRAY_SHULKER_BOX, GREEN_SHULKER_BOX, LIGHT_BLUE_SHULKER_BOX, LIGHT_GRAY_SHULKER_BOX, MAGENTA_SHULKER_BOX, ORANGE_SHULKER_BOX, WHITE_SHULKER_BOX, YELLOW_SHULKER_BOX, PINK_SHULKER_BOX, PURPLE_SHULKER_BOX, PRISMARINE, PRISMARINE_BRICKS_STAIRS, PRISMARINE_STAIRS, STONE_BLOCK_SLAB4, DARK_PRISMARINE_STAIRS, ANVIL, BONE_BLOCK, IRON_TRAPDOOR, NETHER_BRICK_FENCE, CRYING_OBSIDIAN, MAGMA, SMOKER, LIT_SMOKER, HOPPER, REDSTONE_BLOCK, MOB_SPAWNER, NETHERITE_BLOCK, SMOOTH_STONE, DIAMOND_BLOCK, LAPIS_BLOCK, EMERALD_BLOCK, ENCHANTING_TABLE, END_BRICKS, CRACKED_POLISHED_BLACKSTONE_BRICKS, NETHER_BRICK, CRACKED_NETHER_BRICKS, PURPUR_BLOCK, PURPUR_STAIRS, END_BRICK_STAIRS, STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB3, STONE_BRICK_STAIRS, MOSSY_STONE_BRICK_STAIRS, POLISHED_BLACKSTONE_BRICKS, POLISHED_BLACKSTONE_STAIRS, BLACKSTONE_WALL, BLACKSTONE_WALL, POLISHED_BLACKSTONE_WALL, SANDSTONE, GRINDSTONE, SMOOTH_STONE, BREWING_STAND, IRON_CHAIN, LANTERN, SOUL_LANTERN, ANCIENT_DEBRIS, QUARTZ_ORE, NETHERRACK, BASALT, POLISHED_BASALT, STONE_BRICKS, WARPED_NYLIUM, CRIMSON_NYLIUM, END_STONE, ENDER_CHEST, QUARTZ_BLOCK, QUARTZ_STAIRS, QUARTZ_BRICKS, QUARTZ_STAIRS, NETHER_GOLD_ORE, FURNACE, BLAST_FURNACE, LIT_FURNACE, BLAST_FURNACE, BLACKSTONE, BLACK_CONCRETE, BLUE_CONCRETE, BROWN_CONCRETE, CYAN_CONCRETE, GRAY_CONCRETE, GREEN_CONCRETE, LIGHT_BLUE_CONCRETE, LIME_CONCRETE, MAGENTA_CONCRETE, ORANGE_CONCRETE, PINK_CONCRETE, PURPLE_CONCRETE, RED_CONCRETE, LIGHT_GRAY_CONCRETE, WHITE_CONCRETE, YELLOW_CONCRETE, DEEPSLATE_COPPER_ORE, DEEPSLATE_LAPIS_ORE, CHISELED_DEEPSLATE, COBBLED_DEEPSLATE, COBBLED_DEEPSLATE_DOUBLE_SLAB, COBBLED_DEEPSLATE_SLAB, COBBLED_DEEPSLATE_STAIRS, COBBLED_DEEPSLATE_WALL, CRACKED_DEEPSLATE_BRICKS, CRACKED_DEEPSLATE_TILES, DEEPSLATE, DEEPSLATE_BRICK_DOUBLE_SLAB, DEEPSLATE_BRICK_SLAB, DEEPSLATE_BRICK_STAIRS, DEEPSLATE_BRICK_WALL, DEEPSLATE_BRICKS, DEEPSLATE_TILE_DOUBLE_SLAB, DEEPSLATE_TILE_SLAB, DEEPSLATE_TILE_STAIRS, DEEPSLATE_TILE_WALL, DEEPSLATE_TILES, INFESTED_DEEPSLATE, POLISHED_DEEPSLATE, POLISHED_DEEPSLATE_DOUBLE_SLAB, POLISHED_DEEPSLATE_SLAB, POLISHED_DEEPSLATE_STAIRS, POLISHED_DEEPSLATE_WALL, CALCITE, AMETHYST_BLOCK, AMETHYST_CLUSTER, BUDDING_AMETHYST, RAW_COPPER_BLOCK, RAW_GOLD_BLOCK, RAW_IRON_BLOCK, COPPER_ORE, COPPER_BLOCK, CUT_COPPER, CUT_COPPER_SLAB, CUT_COPPER_STAIRS, DOUBLE_CUT_COPPER_SLAB, EXPOSED_COPPER, EXPOSED_CUT_COPPER, EXPOSED_CUT_COPPER_SLAB, EXPOSED_CUT_COPPER_STAIRS, EXPOSED_DOUBLE_CUT_COPPER_SLAB, OXIDIZED_COPPER, OXIDIZED_CUT_COPPER, OXIDIZED_CUT_COPPER_SLAB, OXIDIZED_CUT_COPPER_STAIRS, OXIDIZED_DOUBLE_CUT_COPPER_SLAB, WEATHERED_COPPER, WEATHERED_CUT_COPPER, WEATHERED_CUT_COPPER_SLAB, WEATHERED_CUT_COPPER_STAIRS, WEATHERED_DOUBLE_CUT_COPPER_SLAB, WAXED_COPPER, WAXED_CUT_COPPER, WAXED_CUT_COPPER_SLAB, WAXED_CUT_COPPER_STAIRS, WAXED_DOUBLE_CUT_COPPER_SLAB, WAXED_EXPOSED_COPPER, WAXED_EXPOSED_CUT_COPPER, WAXED_EXPOSED_CUT_COPPER_SLAB, WAXED_EXPOSED_CUT_COPPER_STAIRS, WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB, WAXED_OXIDIZED_COPPER, WAXED_OXIDIZED_CUT_COPPER, WAXED_OXIDIZED_CUT_COPPER_SLAB, WAXED_OXIDIZED_CUT_COPPER_STAIRS, WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB, WAXED_WEATHERED_COPPER, WAXED_WEATHERED_CUT_COPPER, WAXED_WEATHERED_CUT_COPPER_SLAB, WAXED_WEATHERED_CUT_COPPER_STAIRS, WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB, DRIPSTONE_BLOCK, POINTED_DRIPSTONE, LIGHTNING_ROD, BASALT, TUFF, DOUBLE_STONE_BLOCK_SLAB, DOUBLE_STONE_BLOCK_SLAB2, DOUBLE_STONE_BLOCK_SLAB3, DOUBLE_STONE_BLOCK_SLAB4, BLACKSTONE_DOUBLE_SLAB, POLISHED_BLACKSTONE_BRICK_DOUBLE_SLAB, POLISHED_BLACKSTONE_DOUBLE_SLAB, MOSSY_COBBLESTONE_STAIRS, STONECUTTER, STONECUTTER_BLOCK, RED_NETHER_BRICK, RED_NETHER_BRICK_STAIRS, NORMAL_STONE_STAIRS, SMOOTH_BASALT, STONE, COBBLESTONE, MOSSY_COBBLESTONE, DRIPSTONE_BLOCK, BRICK_BLOCK, STONE_STAIRS, STONE_BLOCK_SLAB2, STONE_BLOCK_SLAB3, STONE_BLOCK_SLAB4, COBBLESTONE_WALL, GOLD_BLOCK, IRON_BLOCK, CAULDRON, IRON_BARS, OBSIDIAN, COAL_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_DIAMOND_ORE, DEEPSLATE_EMERALD_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_REDSTONE_ORE, LIT_DEEPSLATE_REDSTONE_ORE, DIAMOND_ORE, EMERALD_ORE, GOLD_ORE, IRON_ORE, LAPIS_ORE, REDSTONE_ORE, LIT_REDSTONE_ORE, RAW_IRON_BLOCK, RAW_GOLD_BLOCK, RAW_COPPER_BLOCK, MUD_BRICK_DOUBLE_SLAB, MUD_BRICK_SLAB, MUD_BRICK_STAIRS, MUD_BRICK_WALL, MUD_BRICKS, HARDENED_CLAY, BLACK_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA, CYAN_TERRACOTTA, GRAY_TERRACOTTA, GREEN_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, LIME_TERRACOTTA, MAGENTA_TERRACOTTA, ORANGE_TERRACOTTA, PINK_TERRACOTTA, PURPLE_TERRACOTTA, RED_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, WHITE_TERRACOTTA, YELLOW_TERRACOTTA, POLISHED_DIORITE_STAIRS, ANDESITE_STAIRS, POLISHED_ANDESITE_STAIRS, GRANITE_STAIRS, POLISHED_GRANITE_STAIRS, POLISHED_BLACKSTONE, CHISELED_POLISHED_BLACKSTONE, POLISHED_BLACKSTONE_BRICK_STAIRS, BLACKSTONE_STAIRS, POLISHED_BLACKSTONE_BRICK_WALL, GILDED_BLACKSTONE, COAL_BLOCK)) {
                pickaxeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_PICKAXE, pickaxeBlocks);

            for (var name : List.of(CHEST, BOOKSHELF, MELON_BLOCK, WARPED_STEM, CRIMSON_STEM, WARPED_STEM, CRIMSON_STEM, CRAFTING_TABLE, CRIMSON_PLANKS, WARPED_PLANKS, WARPED_STAIRS, WARPED_TRAPDOOR, CRIMSON_STAIRS, CRIMSON_TRAPDOOR, CRIMSON_DOOR, CRIMSON_DOUBLE_SLAB, WARPED_DOOR, WARPED_DOUBLE_SLAB, CRAFTING_TABLE, COMPOSTER, CARTOGRAPHY_TABLE, LECTERN, STRIPPED_CRIMSON_STEM, STRIPPED_WARPED_STEM, TRAPDOOR, SPRUCE_TRAPDOOR, BIRCH_TRAPDOOR, JUNGLE_TRAPDOOR, ACACIA_TRAPDOOR, DARK_OAK_TRAPDOOR, WOODEN_DOOR, SPRUCE_DOOR, BIRCH_DOOR, JUNGLE_DOOR, ACACIA_DOOR, DARK_OAK_DOOR, ACACIA_FENCE, DARK_OAK_FENCE, BAMBOO_FENCE, MANGROVE_FENCE, NETHER_BRICK_FENCE, OAK_FENCE, CRIMSON_FENCE, JUNGLE_FENCE, CHERRY_FENCE, BIRCH_FENCE, WARPED_FENCE, SPRUCE_FENCE, FENCE_GATE, SPRUCE_FENCE_GATE, BIRCH_FENCE_GATE, JUNGLE_FENCE_GATE, ACACIA_FENCE_GATE, DARK_OAK_FENCE_GATE, MANGROVE_LOG, OAK_LOG, JUNGLE_LOG, SPRUCE_LOG, DARK_OAK_LOG, CHERRY_LOG, ACACIA_LOG, BIRCH_LOG, ACACIA_PLANKS, BAMBOO_PLANKS, CRIMSON_PLANKS, BIRCH_PLANKS, DARK_OAK_PLANKS, CHERRY_PLANKS, WARPED_PLANKS, OAK_PLANKS, SPRUCE_PLANKS, MANGROVE_PLANKS, JUNGLE_PLANKS, ACACIA_DOUBLE_SLAB, ACACIA_SLAB, BAMBOO_DOUBLE_SLAB, BAMBOO_MOSAIC_SLAB, BIRCH_DOUBLE_SLAB
                    , BIRCH_SLAB, OAK_STAIRS, SPRUCE_STAIRS, BIRCH_STAIRS, JUNGLE_STAIRS, ACACIA_STAIRS, DARK_OAK_STAIRS, WALL_SIGN, SPRUCE_WALL_SIGN, BIRCH_WALL_SIGN, JUNGLE_WALL_SIGN, ACACIA_WALL_SIGN, DARKOAK_WALL_SIGN, WOODEN_PRESSURE_PLATE, SPRUCE_PRESSURE_PLATE, BIRCH_PRESSURE_PLATE, JUNGLE_PRESSURE_PLATE, ACACIA_PRESSURE_PLATE, DARK_OAK_PRESSURE_PLATE, SMITHING_TABLE, FLETCHING_TABLE, BARREL, BEEHIVE, BEE_NEST, LADDER, PUMPKIN, CARVED_PUMPKIN, LIT_PUMPKIN, MANGROVE_DOOR, MANGROVE_DOUBLE_SLAB, MANGROVE_FENCE, MANGROVE_FENCE_GATE, MANGROVE_LOG, MANGROVE_PLANKS, MANGROVE_PRESSURE_PLATE, MANGROVE_SLAB, MANGROVE_STAIRS, MANGROVE_WALL_SIGN, MANGROVE_WOOD, WOODEN_BUTTON, SPRUCE_BUTTON, BIRCH_BUTTON, JUNGLE_BUTTON, ACACIA_BUTTON, DARK_OAK_BUTTON, MANGROVE_BUTTON, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_MANGROVE_WOOD, STRIPPED_OAK_LOG, STRIPPED_SPRUCE_LOG, STRIPPED_BIRCH_LOG, STRIPPED_JUNGLE_LOG, STRIPPED_ACACIA_LOG, STRIPPED_DARK_OAK_LOG, STRIPPED_MANGROVE_LOG, STANDING_SIGN, SPRUCE_STANDING_SIGN, BIRCH_STANDING_SIGN, JUNGLE_STANDING_SIGN, ACACIA_STANDING_SIGN, DARKOAK_STANDING_SIGN, MANGROVE_STANDING_SIGN, MANGROVE_TRAPDOOR, WARPED_STANDING_SIGN, WARPED_WALL_SIGN, CRIMSON_STANDING_SIGN, CRIMSON_WALL_SIGN, MANGROVE_ROOTS)) {
                axeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_AXE, axeBlocks);

            for (var name : List.of(SOUL_SAND, SOUL_SOIL, DIRT_WITH_ROOTS, MYCELIUM, PODZOL, DIRT, FARMLAND, SAND, GRAVEL, GRASS_BLOCK, GRASS_PATH, SNOW, MUD, PACKED_MUD, CLAY)) {
                shovelBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_SHOVEL, shovelBlocks);

            for (var name : List.of(NETHER_WART_BLOCK, HAY_BLOCK, TARGET, SHROOMLIGHT, BlockID.ACACIA_LEAVES,
                    BlockID.AZALEA_LEAVES,
                    BlockID.BIRCH_LEAVES,
                    BlockID.AZALEA_LEAVES_FLOWERED,
                    BlockID.CHERRY_LEAVES,
                    BlockID.DARK_OAK_LEAVES,
                    BlockID.JUNGLE_LEAVES,
                    BlockID.MANGROVE_LEAVES,
                    BlockID.OAK_LEAVES,
                    BlockID.SPRUCE_LEAVES, AZALEA_LEAVES_FLOWERED, AZALEA_LEAVES, WARPED_WART_BLOCK)) {
                hoeBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_HOE, hoeBlocks);

            for (var name : List.of(WEB, BAMBOO)) {
                swordBlocks.put(name, new DigProperty());
            }
            toolBlocks.put(ItemTags.IS_SWORD, swordBlocks);
        }

        private ToolBuilder(ItemCustomTool item) {
            super(item);
            this.item = item;
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("enchantable_value", item.getEnchantAbility());

            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putFloat("mining_speed", 1f)
                    .putBoolean("can_destroy_in_creative", true);
        }

        public ToolBuilder addRepairItemName(@NotNull String repairItemName, String molang) {
            super.addRepairs(List.of(repairItemName), molang);
            return this;
        }

        public ToolBuilder addRepairItemName(@NotNull String repairItemName, int repairAmount) {
            super.addRepairs(List.of(repairItemName), String.valueOf(repairAmount));
            return this;
        }

        public ToolBuilder addRepairItems(@NotNull List<Item> repairItems, String molang) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), molang);
            return this;
        }

        public ToolBuilder addRepairItems(@NotNull List<Item> repairItems, int repairAmount) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), String.valueOf(repairAmount));
            return this;
        }

        /**
         * Controls the mining speed of gathering tools
         *
         * @param speed Mining speed
         */
        public ToolBuilder speed(int speed) {
            if (speed < 0) {
                log.error("speed has an invalid value!");
                return this;
            }
            if (item.isPickaxe() || item.isShovel() || item.isHoe() || item.isAxe() || item.isShears()) {
                this.speed = speed;
            }
            return this;
        }

        /**
         * Add a diggable block to the tool and define dig speed
         *
         * @param blockName the block name
         * @param speed     Mining speed
         * @return the tool builder
         */

        public ToolBuilder addExtraBlock(@NotNull String blockName, int speed) {
            if (speed < 0) {
                log.error("speed has an invalid value!");
                return this;
            }
            this.blocks.add(new CompoundTag()
                    .putCompound("block", new CompoundTag()
                            .putString("name", blockName)
                            .putCompound("states", new CompoundTag())
                            .putString("tags", "")
                    )
                    .putInt("speed", speed));
            return this;
        }

        /**
         * Add a diggable block to the tool and define dig speed
         *
         * @param blocks the blocks
         * @return the tool builder
         */

        public ToolBuilder addExtraBlocks(@NotNull Map<String, Integer> blocks) {
            blocks.forEach((blockName, speed) -> {
                if (speed < 0) {
                    log.error("speed has an invalid value!");
                    return;
                }
                this.blocks.add(new CompoundTag()
                        .putCompound("block", new CompoundTag()
                                .putString("name", blockName)
                                .putCompound("states", new CompoundTag())
                                .putString("tags", "")
                        )
                        .putInt("speed", speed));
            });
            return this;
        }

        /**
         * Add a diggable block to the tool and define dig speed
         *
         * @param blockName the block name
         * @param property  the property
         * @return the tool builder
         */

        public ToolBuilder addExtraBlocks(@NotNull String blockName, DigProperty property) {
            Integer propertySpeed;
            if ((propertySpeed = property.getSpeed()) != null && propertySpeed < 0) {
                log.error("speed has an invalid value!");
                return this;
            }
            this.blocks.add(new CompoundTag()
                    .putCompound("block", new CompoundTag()
                            .putString("name", blockName)
                            .putCompound("states", property.getStates())
                            .putString("tags", "")
                    )
                    .putInt("speed", propertySpeed));
            return this;
        }

        /**
         * Add a class of block to the tool that can be mined, described by blockTag, and the speed to mine them is the speed of {@link #speed(int)}, or the speed corresponding to the tool TIER if it is not defined
         *
         * @param blockTags Block tags
         * @return the tool builder
         */

        public ToolBuilder addExtraBlockTags(@NotNull List<String> blockTags) {
            if (!blockTags.isEmpty()) {
                this.blockTags.addAll(blockTags);
            }
            return this;
        }

        @Override
        public CustomItemDefinition build() {
            // Additional durability Attack damage information
            this.nbt.getCompound("components")
                    .putCompound("minecraft:durability", new CompoundTag().putInt("max_durability", item.getMaxDurability()))
                    .getCompound("item_properties")
                    .putInt("damage", item.getAttackDamage());

            if (speed == null) {
                speed = switch (item.getTier()) {
                    case 6 -> 7;
                    case 5 -> 6;
                    case 4 -> 5;
                    case 3 -> 4;
                    case 2 -> 3;
                    case 1 -> 2;
                    default -> 1;
                };
            }
            String type = null;
            if (item.isPickaxe()) {
                // Added mineable block tags
                this.blockTags.addAll(List.of("'stone'", "'metal'", "'diamond_pick_diggable'", "'mob_spawner'", "'rail'", "'slab_block'", "'stair_block'", "'smooth stone slab'", "'sandstone slab'", "'cobblestone slab'", "'brick slab'", "'stone bricks slab'", "'quartz slab'", "'nether brick slab'"));
                // Added mineable blocks
                type = ItemTags.IS_PICKAXE;
                // Additional enchantment information
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "pickaxe");
                this.tag("minecraft:is_pickaxe");
            } else if (item.isAxe()) {
                this.blockTags.addAll(List.of("'wood'", "'pumpkin'", "'plant'"));
                type = ItemTags.IS_AXE;
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "axe");
                this.tag("minecraft:is_axe");
            } else if (item.isShovel()) {
                this.blockTags.addAll(List.of("'sand'", "'dirt'", "'gravel'", "'grass'", "'snow'"));
                type = ItemTags.IS_SHOVEL;
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "shovel");
                this.tag("minecraft:is_shovel");
            } else if (item.isHoe()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "hoe");
                type = ItemTags.IS_HOE;
                this.tag("minecraft:is_hoe");
            } else if (item.isSword()) {
                this.nbt.getCompound("components").getCompound("item_properties")
                        .putString("enchantable_slot", "sword");
                type = ItemTags.IS_SWORD;
            } else {
                if (this.nbt.getCompound("components").contains("item_tags")) {
                    var list = this.nbt.getCompound("components").getList("item_tags", StringTag.class).getAll();
                    for (var tag : list) {
                        var id = tag.parseValue();
                        if (toolBlocks.containsKey(id)) {
                            type = id;
                            break;
                        }
                    }
                }
            }
            if (type != null) {
                toolBlocks.get(type).forEach(
                        (k, v) -> {
                            if (v.getSpeed() == null) v.setSpeed(speed);
                            blocks.add(new CompoundTag()
                                    .putCompound("block", new CompoundTag()
                                            .putString("name", k)
                                            .putCompound("states", v.getStates())
                                            .putString("tags", "")
                                    )
                                    .putInt("speed", v.getSpeed()));
                        }
                );
            }
            // Added mineable block tags
            if (!this.blockTags.isEmpty()) {
                var cmp = new CompoundTag();
                cmp.putCompound("block", new CompoundTag()
                                .putString("name", "")
                                .putCompound("states", new CompoundTag())
                                .putString("tags", "q.any_tag(" + String.join(", ", this.blockTags) + ")")
                        )
                        .putInt("speed", speed);
                this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(cmp);
                this.nbt.getCompound("components")
                        .putCompound("minecraft:digger", this.diggerRoot);
            }
            if (!this.blocks.isEmpty()) {
                // Added mineable blocks
                for (var k : this.blocks) {
                    this.diggerRoot.getList("destroy_speeds", CompoundTag.class).add(k);
                }
                if (!this.nbt.getCompound("components").containsCompound("minecraft:digger")) {
                    this.nbt.getCompound("components")
                            .putCompound("minecraft:digger", this.diggerRoot);
                }
            }
            return buildDefinition();
        }
    }



    /**
     * Definition builder for custom armor
     *
     * @param item the item
     */
    @Deprecated
    public static CustomItemDefinition.ArmorBuilder armorBuilder(ItemCustomArmor item) {
        return new CustomItemDefinition.ArmorBuilder(item);
    }

    @Deprecated
    public static class ArmorBuilder extends SimpleBuilder {
        private final ItemCustomArmor item;

        private ArmorBuilder(ItemCustomArmor item) {
            super(item);
            this.item = item;
        }

        @Deprecated
        public ArmorBuilder addRepairItemName(@NotNull String repairItemName, String molang) {
            super.addRepairs(List.of(repairItemName), molang);
            return this;
        }

        @Deprecated
        public ArmorBuilder addRepairItemName(@NotNull String repairItemName, int repairAmount) {
            super.addRepairs(List.of(repairItemName), String.valueOf(repairAmount));
            return this;
        }

        @Deprecated
        public ArmorBuilder addRepairItems(@NotNull List<Item> repairItems, String molang) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), molang);
            return this;
        }

        @Deprecated
        public ArmorBuilder addRepairItems(@NotNull List<Item> repairItems, int repairAmount) {
            super.addRepairs(repairItems.stream().map(Item::getId).toList(), String.valueOf(repairAmount));
            return this;
        }

        @Override
        @Deprecated
        public CustomItemDefinition build() {
            ensureItemProperties()
                    .putInt("enchantable_value", item.getEnchantAbility())
                    .putBoolean("can_destroy_in_creative", true)
                    .putInt("max_stack_size", 1)
                    .putInt("damage", 0)
                    .putBoolean("should_despawn", true)
                    .putFloat("mining_speed", 1.0f)
                    .putBoolean("stacked_by_data", false);

            CompoundTag components = nbt.getCompound("components");
            if (item.getMaxDurability() > 0) {
                CompoundTag durability = new CompoundTag()
                        .putInt("max_durability", item.getMaxDurability())
                        .putCompound("damage_chance", new CompoundTag()
                                .putInt("min", item.getDamageChanceMin())
                                .putInt("max", item.getDamageChanceMax()));
                components.putCompound("minecraft:durability", durability);
            }

            CompoundTag wearable = new CompoundTag().putInt("protection", item.getArmorPoints());
            CompoundTag enchantable = new CompoundTag()
                    .putByte("value", (byte) item.getEnchantAbility());

            if (item.isHelmet()) {
                ensureItemProperties().putString("enchantable_slot", "armor_head");
                wearable.putString("slot", "slot.armor.head");
                enchantable.putString("slot", "armor_head");
            } else if (item.isChestplate()) {
                ensureItemProperties().putString("enchantable_slot", "armor_torso");
                wearable.putString("slot", "slot.armor.chest");
                enchantable.putString("slot", "armor_torso");
            } else if (item.isLeggings()) {
                ensureItemProperties().putString("enchantable_slot", "armor_legs");
                wearable.putString("slot", "slot.armor.legs");
                enchantable.putString("slot", "armor_legs");
            } else if (item.isBoots()) {
                ensureItemProperties().putString("enchantable_slot", "armor_feet");
                wearable.putString("slot", "slot.armor.feet");
                enchantable.putString("slot", "armor_feet");
            }

            components.putCompound("minecraft:wearable", wearable);
            components.putCompound("minecraft:enchantable", enchantable);
            return buildDefinition();
        }
    }



    /**
     * Definition builder for custom food or potion.
     * @deprecated Use {@link #simpleBuilder(ItemCustom)} together with
     * {@link SimpleBuilder#food(boolean, int, float)},
     * {@link SimpleBuilder#useAnimation(String)} and
     * {@link SimpleBuilder#useModifiers(float, float)}.
     */
    @Deprecated
    public static CustomItemDefinition.EdibleBuilder edibleBuilder(ItemCustomFood item) {
        return new CustomItemDefinition.EdibleBuilder(item);
    }

    /**
     * @deprecated Compatibility shim. Prefer {@link SimpleBuilder#food(boolean, int, float)},
     * {@link SimpleBuilder#useAnimation(String)} and {@link SimpleBuilder#useModifiers(float, float)}.
     */
    @Deprecated
    public static class EdibleBuilder extends SimpleBuilder {
        private EdibleBuilder(ItemCustomFood item) {
            super(item);

            if (this.nbt.getCompound("components").contains("minecraft:food")) {
                this.nbt.getCompound("components").getCompound("minecraft:food").putBoolean("can_always_eat", item.canAlwaysEat());
            } else {
                this.nbt.getCompound("components").putCompound("minecraft:food", new CompoundTag().putBoolean("can_always_eat", item.canAlwaysEat()));
            }

            int eatingtick = item.getUsingTicks();
            this.nbt.getCompound("components")
                    .getCompound("item_properties")
                    .putInt("use_duration", eatingtick)
                    .putInt("use_animation", item.isDrink() ? 2 : 1)
                    .putBoolean("can_destroy_in_creative", true);
        }
    }




    // Helpers
    /** Used for spawn eggs registration only */
    public static int ensureRuntimeIdAllocated(String idStr) {
        if (!INTERNAL_ALLOCATION_ID_MAP.containsKey(idStr)) {
            int id;
            do {
                id = nextRuntimeId.getAndIncrement();
            } while (INTERNAL_ALLOCATION_ID_MAP.containsValue(id));
            INTERNAL_ALLOCATION_ID_MAP.put(idStr, id);
        }
        return INTERNAL_ALLOCATION_ID_MAP.getInt(idStr);
    }

    @Nullable
    public BlockPlacerData getBlockPlacerData() {
        CompoundTag components = nbt.getCompound("components");
        if (!components.contains("minecraft:block_placer")) {
            return null;
        }

        CompoundTag placer = components.getCompound("minecraft:block_placer");
        String blockId = placer.getString("block");
        List<String> useOnList = new ArrayList<>();

        ListTag<CompoundTag> useOnTag = placer.getList("use_on", CompoundTag.class);
        if (useOnTag != null && useOnTag.size() > 0) {
            for (int i = 0; i < useOnTag.size(); i++) {
                CompoundTag entry = useOnTag.get(i);
                String useOnName = entry.getString("name");
                if (!useOnName.isEmpty()) {
                useOnList.add(useOnName);
                }
            }
        }

        return new BlockPlacerData(blockId, useOnList);
    }

    public @Nullable String getDisplayName() {
        if (!this.nbt.getCompound("components").contains("minecraft:display_name")) return null;
        return this.nbt.getCompound("components").getCompound("minecraft:display_name").getString("value");
    }

    public String getTexture() {
        return this.nbt.getCompound("components")
                .getCompound("item_properties")
                .getCompound("minecraft:icon")
                .getCompound("textures")
                .getString("default");
    }

    public int getRuntimeId() {
        return CustomItemDefinition.INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
    }

    public static int getRuntimeId(String identifier) {
        return CustomItemDefinition.INTERNAL_ALLOCATION_ID_MAP.getInt(identifier);
    }

    public CompoundTag getComponents() {
        return this.nbt.getCompound("components");
    }

    public boolean hasComponent(String key) {
        CompoundTag comps = getComponents();
        return comps != null && comps.contains(key);
    }

    public CompoundTag getComponent(String key) {
        CompoundTag comps = getComponents();
        return (comps != null && comps.contains(key)) ? comps.getCompound(key) : null;
    }

    public CompoundTag getItemProperties() {
        CompoundTag comps = getComponents();
        return comps != null ? comps.getCompound("item_properties") : null;
    }

    public boolean isEdible() {
        return hasComponent("minecraft:food");
    }

    public boolean isWearable() {
        return getWearableType() != ItemArmorType.NONE;
    }

    public @NotNull ItemArmorType getWearableType() {
        CompoundTag wearable = getComponent("minecraft:wearable");
        if (wearable == null) return ItemArmorType.NONE;

        String slot = wearable.getString("slot");
        return ItemArmorType.get(slot);
    }
    public boolean isHelmet()     { return getWearableType() == ItemArmorType.HEAD; }
    public boolean isChestplate() { return getWearableType() == ItemArmorType.CHEST; }
    public boolean isLeggings()   { return getWearableType() == ItemArmorType.LEGS; }
    public boolean isBoots()      { return getWearableType() == ItemArmorType.FEET; }

    public @Nullable ItemEnchantSlot getEnchantSlot() {
        CompoundTag itemProps = getComponent("item_properties");
        if (itemProps == null) return null;

        String slot = itemProps.getString("enchantable_slot");
        if (slot == null || slot.isBlank()) return null;

        return ItemEnchantSlot.fromId(slot);
    }
    public boolean isSword()     { return getEnchantSlot() == ItemEnchantSlot.SWORD; }
    public boolean isShield()    { return getEnchantSlot() == ItemEnchantSlot.SHIELD; }
    public boolean isPickaxe()   { return getEnchantSlot() == ItemEnchantSlot.PICKAXE; }
    public boolean isShovel()    { return getEnchantSlot() == ItemEnchantSlot.SHOVEL; }
    public boolean isAxe()       { return getEnchantSlot() == ItemEnchantSlot.AXE; }
    public boolean isHoe()       { return getEnchantSlot() == ItemEnchantSlot.HOE; }
    public boolean isShears()    { return getEnchantSlot() == ItemEnchantSlot.SHEARS; }
    public boolean isBow()       { return getEnchantSlot() == ItemEnchantSlot.BOW; }
    public boolean isCrossbow()  { return getEnchantSlot() == ItemEnchantSlot.CROSSBOW; }
    public boolean isTrident()   { return getEnchantSlot() == ItemEnchantSlot.SPEAR; }

    public int wearableProtection() {
        CompoundTag wearable = getComponent("minecraft:wearable");
        return wearable != null ? wearable.getInt("protection") : 0;
    }

    public boolean hidesPlayerLocation() {
        CompoundTag wearable = getComponent("minecraft:wearable");
        return wearable != null && wearable.getBoolean("hides_player_location");
    }

    public boolean canTakeDamage() {
        return hasComponent("minecraft:durability");
    }

    public int maxDurability() {
        return hasComponent("minecraft:durability") ?
            getComponent("minecraft:durability").getInt("max_durability")
            : 0;
    }

    public int damageChanceMin() {
        return hasComponent("minecraft:durability")
            ? getComponent("minecraft:durability").getCompound("damage_chance").getInt("min")
            : 100;
    }

    public int damageChanceMax() {
        return hasComponent("minecraft:durability")
            ? getComponent("minecraft:durability").getCompound("damage_chance").getInt("max")
            : 100;
    }

    public CompoundTag getNbt() {
        return this.nbt;
    }
}