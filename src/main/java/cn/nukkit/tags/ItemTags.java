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

    public final static String $1 = "minecraft:arrow";
    public final static String $2 = "minecraft:banner";
    public final static String $3 = "minecraft:boat";
    public final static String $4 = "minecraft:boats";
    public final static String $5 = "minecraft:chainmail_tier";
    public final static String $6 = "minecraft:coals";
    public final static String $7 = "minecraft:crimson_stems";
    public final static String $8 = "minecraft:diamond_tier";
    public final static String $9 = "minecraft:digger";
    public final static String $10 = "minecraft:door";
    public final static String $11 = "minecraft:golden_tier";
    public final static String $12 = "minecraft:hanging_actor";
    public final static String $13 = "minecraft:hanging_sign";
    public final static String $14 = "minecraft:horse_armor";
    public final static String $15 = "minecraft:iron_tier";
    public final static String $16 = "minecraft:is_armor";
    public final static String $17 = "minecraft:is_axe";
    public final static String $18 = "minecraft:is_cooked";
    public final static String $19 = "minecraft:is_fish";
    public final static String $20 = "minecraft:is_food";
    public final static String $21 = "minecraft:is_hoe";
    public final static String $22 = "minecraft:is_meat";
    public final static String $23 = "minecraft:is_minecart";
    public final static String $24 = "minecraft:is_pickaxe";
    public final static String $25 = "minecraft:is_shovel";
    public final static String $26 = "minecraft:is_sword";
    public final static String $27 = "minecraft:is_tool";
    public final static String $28 = "minecraft:leather_tier";
    public final static String $29 = "minecraft:lectern_books";
    public final static String $30 = "minecraft:logs";
    public final static String $31 = "minecraft:logs_that_burn";
    public final static String $32 = "minecraft:mangrove_logs";
    public final static String $33 = "minecraft:music_disc";
    public final static String $34 = "minecraft:netherite_tier";
    public final static String $35 = "minecraft:planks";
    public final static String $36 = "minecraft:sand";
    public final static String $37 = "minecraft:sign";
    public final static String $38 = "minecraft:soul_fire_base_blocks";
    public final static String $39 = "minecraft:spawn_egg";
    public final static String $40 = "minecraft:stone_bricks";
    public final static String $41 = "minecraft:stone_crafting_materials";
    public final static String $42 = "minecraft:stone_tier";
    public final static String $43 = "minecraft:stone_tool_materials";
    public final static String $44 = "minecraft:vibration_damper";
    public final static String $45 = "minecraft:warped_stems";
    public final static String $46 = "minecraft:wooden_slabs";
    public final static String $47 = "minecraft:wooden_tier";
    public final static String $48 = "minecraft:wool";

    private static final Object2ObjectOpenHashMap<String, Set<String>> TAG_2_ITEMS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Set<String>> ITEM_2_TAGS = new Object2ObjectOpenHashMap<>();

    static {
        try {
            try (var $49 = Server.class.getClassLoader().getResourceAsStream("item_tags.json")) {
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
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    
    public static void register(String identifier, Collection<String> tags) {
        var $50 = ITEM_2_TAGS.get(identifier);
        if (tagSet != null) tagSet.addAll(tags);
        else ITEM_2_TAGS.put(identifier, new HashSet<>(tags));
        for (var tag : tags) {
            var $51 = TAG_2_ITEMS.get(tag);
            if (itemSet != null) itemSet.add(identifier);
            else TAG_2_ITEMS.put(tag, new HashSet<>(Collections.singleton(identifier)));
        }
    }
}
