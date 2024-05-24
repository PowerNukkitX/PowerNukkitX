package cn.nukkit.tags;

import cn.nukkit.Server;
import cn.nukkit.utils.JSONUtils;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public final class ItemTags {
    ItemTags() {
    }

    public final static String ARROW = "minecraft:arrow";
    public final static String BANNER = "minecraft:banner";
    public final static String BOAT = "minecraft:boat";
    public final static String BOATS = "minecraft:boats";
    public final static String CHAINMAIL_TIER = "minecraft:chainmail_tier";
    public final static String COALS = "minecraft:coals";
    public final static String CRIMSON_STEMS = "minecraft:crimson_stems";
    public final static String DIAMOND_TIER = "minecraft:diamond_tier";
    public final static String DIGGER = "minecraft:digger";
    public final static String DOOR = "minecraft:door";
    public final static String GOLDEN_TIER = "minecraft:golden_tier";
    public final static String HANGING_ACTOR = "minecraft:hanging_actor";
    public final static String HANGING_SIGN = "minecraft:hanging_sign";
    public final static String HORSE_ARMOR = "minecraft:horse_armor";
    public final static String IRON_TIER = "minecraft:iron_tier";
    public final static String IS_ARMOR = "minecraft:is_armor";
    public final static String IS_AXE = "minecraft:is_axe";
    public final static String IS_COOKED = "minecraft:is_cooked";
    public final static String IS_FISH = "minecraft:is_fish";
    public final static String IS_FOOD = "minecraft:is_food";
    public final static String IS_HOE = "minecraft:is_hoe";
    public final static String IS_MEAT = "minecraft:is_meat";
    public final static String IS_MINECART = "minecraft:is_minecart";
    public final static String IS_PICKAXE = "minecraft:is_pickaxe";
    public final static String IS_SHOVEL = "minecraft:is_shovel";
    public final static String IS_SWORD = "minecraft:is_sword";
    public final static String IS_TOOL = "minecraft:is_tool";
    public final static String LEATHER_TIER = "minecraft:leather_tier";
    public final static String LECTERN_BOOKS = "minecraft:lectern_books";
    public final static String LOGS = "minecraft:logs";
    public final static String LOGS_THAT_BURN = "minecraft:logs_that_burn";
    public final static String MANGROVE_LOGS = "minecraft:mangrove_logs";
    public final static String MUSIC_DISC = "minecraft:music_disc";
    public final static String NETHERITE_TIER = "minecraft:netherite_tier";
    public final static String PLANKS = "minecraft:planks";
    public final static String SAND = "minecraft:sand";
    public final static String SIGN = "minecraft:sign";
    public final static String SOUL_FIRE_BASE_BLOCKS = "minecraft:soul_fire_base_blocks";
    public final static String SPAWN_EGG = "minecraft:spawn_egg";
    public final static String STONE_BRICKS = "minecraft:stone_bricks";
    public final static String STONE_CRAFTING_MATERIALS = "minecraft:stone_crafting_materials";
    public final static String STONE_TIER = "minecraft:stone_tier";
    public final static String STONE_TOOL_MATERIALS = "minecraft:stone_tool_materials";
    public final static String VIBRATION_DAMPER = "minecraft:vibration_damper";
    public final static String WARPED_STEMS = "minecraft:warped_stems";
    public final static String WOODEN_SLABS = "minecraft:wooden_slabs";
    public final static String WOODEN_TIER = "minecraft:wooden_tier";
    public final static String WOOL = "minecraft:wool";

    private static final Object2ObjectOpenHashMap<String, Set<String>> TAG_2_ITEMS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Set<String>> ITEM_2_TAGS = new Object2ObjectOpenHashMap<>();

    static {
        try {
            try (var stream = Server.class.getClassLoader().getResourceAsStream("item_tags.json")) {
                TypeToken<HashMap<String, HashSet<String>>> typeToken = new TypeToken<>() {
                };
                assert stream != null;
                HashMap<String, HashSet<String>> map = JSONUtils.from(new InputStreamReader(stream), typeToken);
                TAG_2_ITEMS.putAll(map);
                for (var e : TAG_2_ITEMS.entrySet()) {
                    for (var item : e.getValue()) {
                        Set<String> tags = ITEM_2_TAGS.computeIfAbsent(item, (k) -> new HashSet<>());
                        tags.add(e.getKey());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void trim() {
        TAG_2_ITEMS.trim();
        ITEM_2_TAGS.trim();
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getTagSet(String identifier) {
        return Collections.unmodifiableSet(ITEM_2_TAGS.getOrDefault(identifier, Set.of()));
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getItemSet(String tag) {
        return Collections.unmodifiableSet(TAG_2_ITEMS.getOrDefault(tag, Set.of()));
    }

    /**
     * Register item tags for the given item identifier.
     * This is a server-side only method, DO NOT affect the client.
     *
     * @param identifier The item identifier
     * @param tags       The tags to register
     */
    public static void register(String identifier, Collection<String> tags) {
        var tagSet = ITEM_2_TAGS.get(identifier);
        if (tagSet != null) tagSet.addAll(tags);
        else ITEM_2_TAGS.put(identifier, new HashSet<>(tags));
        for (var tag : tags) {
            var itemSet = TAG_2_ITEMS.get(tag);
            if (itemSet != null) itemSet.add(identifier);
            else TAG_2_ITEMS.put(tag, new HashSet<>(Collections.singleton(identifier)));
        }
    }
}
