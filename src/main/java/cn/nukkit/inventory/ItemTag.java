package cn.nukkit.inventory;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Identifier;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.19.50-r2")
public final class ItemTag {
    public static final Identifier ARROW = new Identifier("minecraft:arrow");
    public static final Identifier BANNER = new Identifier("minecraft:banner");
    public static final Identifier BOAT = new Identifier("minecraft:boat");
    public static final Identifier BOATS = new Identifier("minecraft:boats");
    public static final Identifier CHAINMAIL_TIER = new Identifier("minecraft:chainmail_tier");
    public static final Identifier COALS = new Identifier("minecraft:coals");
    public static final Identifier CRIMSON_STEMS = new Identifier("minecraft:crimson_stems");
    public static final Identifier DIAMOND_TIER = new Identifier("minecraft:diamond_tier");
    public static final Identifier DIGGER = new Identifier("minecraft:digger");
    public static final Identifier DOOR = new Identifier("minecraft:door");
    public static final Identifier GOLDEN_TIER = new Identifier("minecraft:golden_tier");
    public static final Identifier HANGING_ACTOR = new Identifier("minecraft:hanging_actor");
    public static final Identifier HORSE_ARMOR = new Identifier("minecraft:horse_armor");
    public static final Identifier IRON_TIER = new Identifier("minecraft:iron_tier");
    public static final Identifier IS_ARMOR = new Identifier("minecraft:is_armor");
    public static final Identifier IS_AXE = new Identifier("minecraft:is_axe");
    public static final Identifier IS_COOKED = new Identifier("minecraft:is_cooked");
    public static final Identifier IS_FISH = new Identifier("minecraft:is_fish");
    public static final Identifier IS_FOOD = new Identifier("minecraft:is_food");
    public static final Identifier IS_HOE = new Identifier("minecraft:is_hoe");
    public static final Identifier IS_MEAT = new Identifier("minecraft:is_meat");
    public static final Identifier IS_MINECART = new Identifier("minecraft:is_minecart");
    public static final Identifier IS_PICKAXE = new Identifier("minecraft:is_pickaxe");
    public static final Identifier IS_SHOVEL = new Identifier("minecraft:is_shovel");
    public static final Identifier IS_SWORD = new Identifier("minecraft:is_sword");
    public static final Identifier IS_TOOL = new Identifier("minecraft:is_tool");
    public static final Identifier LEATHER_TIER = new Identifier("minecraft:leather_tier");
    public static final Identifier LECTERN_BOOKS = new Identifier("minecraft:lectern_books");
    public static final Identifier LOGS = new Identifier("minecraft:logs");
    public static final Identifier LOGS_THAT_BURN = new Identifier("minecraft:logs_that_burn");
    public static final Identifier MANGROVE_LOGS = new Identifier("minecraft:mangrove_logs");
    public static final Identifier MUSIC_DISC = new Identifier("minecraft:music_disc");
    public static final Identifier NETHERITE_TIER = new Identifier("minecraft:netherite_tier");
    public static final Identifier PLANKS = new Identifier("minecraft:planks");
    public static final Identifier SAND = new Identifier("minecraft:sand");
    public static final Identifier SIGN = new Identifier("minecraft:sign");
    public static final Identifier SOUL_FIRE_BASE_BLOCKS = new Identifier("minecraft:soul_fire_base_blocks");
    public static final Identifier SPAWN_EGG = new Identifier("minecraft:spawn_egg");
    public static final Identifier STONE_BRICKS = new Identifier("minecraft:stone_bricks");
    public static final Identifier STONE_CRAFTING_MATERIALS = new Identifier("minecraft:stone_crafting_materials");
    public static final Identifier STONE_TIER = new Identifier("minecraft:stone_tier");
    public static final Identifier STONE_TOOL_MATERIALS = new Identifier("minecraft:stone_tool_materials");
    public static final Identifier VIBRATION_DAMPER = new Identifier("minecraft:vibration_damper");
    public static final Identifier WARPED_STEMS = new Identifier("minecraft:warped_stems");
    public static final Identifier WOODEN_SLABS = new Identifier("minecraft:wooden_slabs");
    public static final Identifier WOODEN_TIER = new Identifier("minecraft:wooden_tier");
    public static final Identifier WOOL = new Identifier("minecraft:wool");

    private static final Map<String, Set<String>> TAG_2_ITEMS = new HashMap<>();
    private static final Map<String, Set<String>> ITEM_2_TAGS = new HashMap<>();

    static {
        final var config = new Config(Config.JSON);
        try {
            config.load(Server.class.getModule().getResourceAsStream("tag_2_items.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.getAll()
                .forEach((k, v) -> TAG_2_ITEMS.put(
                        k, ((List<?>) v).stream().map(s -> (String) s).collect(Collectors.toSet())));

        try {
            config.load(Server.class.getModule().getResourceAsStream("item_2_tags.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        config.getAll()
                .forEach((k, v) -> ITEM_2_TAGS.put(
                        k, ((List<?>) v).stream().map(s -> (String) s).collect(Collectors.toSet())));
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

    @NotNull public static Set<String> getTagSet(String namespaceId) {
        return Collections.unmodifiableSet(ITEM_2_TAGS.getOrDefault(namespaceId, Set.of()));
    }

    public static List<String> getItems(String tag) {
        var result = TAG_2_ITEMS.get(tag);
        if (result == null) return null;
        return result.stream().toList();
    }

    @NotNull public static Set<String> getItemSet(String tag) {
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
