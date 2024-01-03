package cn.nukkit.tags;

import cn.nukkit.Server;
import cn.nukkit.utils.Identifier;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public final class BlockTags {
    BlockTags() {
    }

    public static final String ACACIA = "acacia";
    public static final String BIRCH = "birch";
    public static final String DARK_OAK = "dark_oak";
    public static final String DIAMOND_PICK_DIGGABLE = "diamond_pick_diggable";
    public static final String DIRT = "dirt";
    public static final String FERTILIZE_AREA = "fertilize_area";
    public static final String GOLD_PICK_DIGGABLE = "gold_pick_diggable";
    public static final String GRASS = "grass";
    public static final String GRAVEL = "gravel";
    public static final String IRON_PICK_DIGGABLE = "iron_pick_diggable";
    public static final String JUNGLE = "jungle";
    public static final String LOG = "log";
    public static final String METAL = "metal";
    public static final String MINECRAFT_CROP = "minecraft:crop";
    public static final String MOB_SPAWNER = "mob_spawner";
    public static final String NOT_FEATURE_REPLACEABLE = "not_feature_replaceable";
    public static final String OAK = "oak";
    public static final String PLANT = "plant";
    public static final String PUMPKIN = "pumpkin";
    public static final String RAIL = "rail";
    public static final String SAND = "sand";
    public static final String SNOW = "snow";
    public static final String SPRUCE = "spruce";
    public static final String STONE = "stone";
    public static final String STONE_PICK_DIGGABLE = "stone_pick_diggable";
    public static final String TEXT_SIGN = "text_sign";
    public static final String WATER = "water";
    public static final String WOOD = "wood";
    public static final String WOOD_PICK_DIGGABLE = "wood_pick_diggable";

    //PNX only
    public static final String PNX_WOOL = "pnx:wool";


    private static final Object2ObjectOpenHashMap<String, Set<String>> TAG_2_BLOCKS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Set<String>> BLOCKS_2_TAGS = new Object2ObjectOpenHashMap<>();

    static {
        try {
            Gson gson = new Gson();
            try (var stream = Server.class.getClassLoader().getResourceAsStream("item_tags.json")) {
                TypeToken<HashMap<String, HashSet<String>>> typeToken = new TypeToken<>() {
                };
                assert stream != null;
                HashMap<String, HashSet<String>> map = gson.fromJson(new InputStreamReader(stream), typeToken.getType());
                HashMap<String, HashSet<String>> map2 = new HashMap<>();
                map.forEach((key, value) -> {
                    HashSet<String> handle = new HashSet<>(value.size());
                    value.forEach(v -> handle.add("minecraft:" + v));
                    map2.put(key, handle);
                });
                TAG_2_BLOCKS.putAll(map2);
                for (var e : TAG_2_BLOCKS.entrySet()) {
                    for (var block : e.getValue()) {
                        Set<String> tags = BLOCKS_2_TAGS.computeIfAbsent(block, (k) -> new HashSet<>());
                        tags.add(e.getKey());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void trim() {
        TAG_2_BLOCKS.trim();
        BLOCKS_2_TAGS.trim();
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getTagSet(final String identifier) {
        return Collections.unmodifiableSet(BLOCKS_2_TAGS.getOrDefault(identifier, Set.of()));
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getBlockSet(final String tag) {
        return Collections.unmodifiableSet(TAG_2_BLOCKS.getOrDefault(tag, Set.of()));
    }

    /**
     * Register Block tags for the given block identifier.
     * This is a server-side only method, DO NOT affect the client.
     *
     * @param identifier The block identifier
     * @param tags       The tags to register
     */
    public static void register(String identifier, Collection<String> tags) {
        register(identifier, true, tags);
    }

    public static void register(String identifier, boolean check, Collection<String> tags) {
        if (check) Identifier.assertValid(identifier);
        final var tagSet = BLOCKS_2_TAGS.get(identifier);
        if (tagSet != null) tagSet.addAll(tags);
        else BLOCKS_2_TAGS.put(identifier, new HashSet<>(tags));
        for (var tag : tags) {
            var itemSet = TAG_2_BLOCKS.get(tag);
            if (itemSet != null) itemSet.add(identifier);
            else TAG_2_BLOCKS.put(tag, new HashSet<>(Collections.singleton(identifier)));
        }
    }
}
