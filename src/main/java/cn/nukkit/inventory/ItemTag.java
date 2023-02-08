package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@PowerNukkitXOnly
@Since("1.19.50-r2")
public final class ItemTag {
    public final static Identifier ARROW = new Identifier("minecraft:arrow");
    public final static Identifier BANNER = new Identifier("minecraft:banner");
    public final static Identifier BOAT = new Identifier("minecraft:boat");
    public final static Identifier BOATS = new Identifier("minecraft:boats");
    public final static Identifier CHAINMAIL_TIER = new Identifier("minecraft:chainmail_tier");
    public final static Identifier COALS = new Identifier("minecraft:coals");
    public final static Identifier CRIMSON_STEMS = new Identifier("minecraft:crimson_stems");
    public final static Identifier DIAMOND_TIER = new Identifier("minecraft:diamond_tier");
    public final static Identifier DIGGER = new Identifier("minecraft:digger");
    public final static Identifier DOOR = new Identifier("minecraft:door");
    public final static Identifier GOLDEN_TIER = new Identifier("minecraft:golden_tier");
    public final static Identifier HANGING_ACTOR = new Identifier("minecraft:hanging_actor");
    public final static Identifier HORSE_ARMOR = new Identifier("minecraft:horse_armor");
    public final static Identifier IRON_TIER = new Identifier("minecraft:iron_tier");
    public final static Identifier IS_ARMOR = new Identifier("minecraft:is_armor");
    public final static Identifier IS_AXE = new Identifier("minecraft:is_axe");
    public final static Identifier IS_COOKED = new Identifier("minecraft:is_cooked");
    public final static Identifier IS_FISH = new Identifier("minecraft:is_fish");
    public final static Identifier IS_FOOD = new Identifier("minecraft:is_food");
    public final static Identifier IS_HOE = new Identifier("minecraft:is_hoe");
    public final static Identifier IS_MEAT = new Identifier("minecraft:is_meat");
    public final static Identifier IS_MINECART = new Identifier("minecraft:is_minecart");
    public final static Identifier IS_PICKAXE = new Identifier("minecraft:is_pickaxe");
    public final static Identifier IS_SHOVEL = new Identifier("minecraft:is_shovel");
    public final static Identifier IS_SWORD = new Identifier("minecraft:is_sword");
    public final static Identifier IS_TOOL = new Identifier("minecraft:is_tool");
    public final static Identifier LEATHER_TIER = new Identifier("minecraft:leather_tier");
    public final static Identifier LECTERN_BOOKS = new Identifier("minecraft:lectern_books");
    public final static Identifier LOGS = new Identifier("minecraft:logs");
    public final static Identifier LOGS_THAT_BURN = new Identifier("minecraft:logs_that_burn");
    public final static Identifier MANGROVE_LOGS = new Identifier("minecraft:mangrove_logs");
    public final static Identifier MUSIC_DISC = new Identifier("minecraft:music_disc");
    public final static Identifier NETHERITE_TIER = new Identifier("minecraft:netherite_tier");
    public final static Identifier PLANKS = new Identifier("minecraft:planks");
    public final static Identifier SAND = new Identifier("minecraft:sand");
    public final static Identifier SIGN = new Identifier("minecraft:sign");
    public final static Identifier SOUL_FIRE_BASE_BLOCKS = new Identifier("minecraft:soul_fire_base_blocks");
    public final static Identifier SPAWN_EGG = new Identifier("minecraft:spawn_egg");
    public final static Identifier STONE_BRICKS = new Identifier("minecraft:stone_bricks");
    public final static Identifier STONE_CRAFTING_MATERIALS = new Identifier("minecraft:stone_crafting_materials");
    public final static Identifier STONE_TIER = new Identifier("minecraft:stone_tier");
    public final static Identifier STONE_TOOL_MATERIALS = new Identifier("minecraft:stone_tool_materials");
    public final static Identifier VIBRATION_DAMPER = new Identifier("minecraft:vibration_damper");
    public final static Identifier WARPED_STEMS = new Identifier("minecraft:warped_stems");
    public final static Identifier WOODEN_SLABS = new Identifier("minecraft:wooden_slabs");
    public final static Identifier WOODEN_TIER = new Identifier("minecraft:wooden_tier");
    public final static Identifier WOOL = new Identifier("minecraft:wool");

    private static final Map<String, Set<String>> TAG_2_ITEMS = new HashMap<>();
    private static final Map<String, Set<String>> ITEM_2_TAGS = new HashMap<>();

    static {
        final var config = new Config(Config.JSON);
        try {
            config.load(Server.class.getModule().getResourceAsStream("tag_2_items.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.getAll().forEach((k, v) -> TAG_2_ITEMS.put(k, ((List<?>) v).stream().map(s -> (String) s).collect(Collectors.toSet())));

        try {
            config.load(Server.class.getModule().getResourceAsStream("item_2_tags.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.getAll().forEach((k, v) -> ITEM_2_TAGS.put(k, ((List<?>) v).stream().map(s -> (String) s).collect(Collectors.toSet())));
    }

    public static Map<String, Set<String>> getTag2Items() {
        return new HashMap<>(TAG_2_ITEMS);
    }

    public static Map<String, Set<String>> getItem2Tags() {
        return new HashMap<>(ITEM_2_TAGS);
    }

    public static List<String> getTags(String namespaceId) {
        var result = ITEM_2_TAGS.get(namespaceId);
        if (result == null) return null;
        return result.stream().toList();
    }

    @NotNull
    public static Set<String> getTagSet(String namespaceId) {
        return Collections.unmodifiableSet(ITEM_2_TAGS.getOrDefault(namespaceId, Set.of()));
    }

    public static List<String> getItems(String tag) {
        var result = TAG_2_ITEMS.get(tag);
        if (result == null) return null;
        return result.stream().toList();
    }

    @NotNull
    public static Set<String> getItemSet(String tag) {
        return Collections.unmodifiableSet(TAG_2_ITEMS.getOrDefault(tag, Set.of()));
    }

    /**
     * Register item tags for the given item namespaceId.
     * This is a server-side only method, DO NOT affect the client.
     *
     * @param namespaceId The item namespaceId
     * @param tags        The tags to register
     */
    @Since("1.19.50-r3")
    @PowerNukkitXOnly
    public static void registerItemTag(String namespaceId, Collection<String> tags) {
        var tagSet = ITEM_2_TAGS.get(namespaceId);
        if (tagSet != null) tagSet.addAll(tags);
        else ITEM_2_TAGS.put(namespaceId, new HashSet<>(tags));
        for (var tag : tags) {
            var itemSet = TAG_2_ITEMS.get(tag);
            if (itemSet != null) itemSet.add(namespaceId);
            else TAG_2_ITEMS.put(tag, new HashSet<>(Collections.singleton(namespaceId)));
        }
    }

}
