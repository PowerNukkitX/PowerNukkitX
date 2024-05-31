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

public final class BlockTags {
    BlockTags() {
    }

    public static final String $1 = "acacia";
    public static final String $2 = "birch";
    public static final String $3 = "dark_oak";
    public static final String $4 = "diamond_pick_diggable";
    public static final String $5 = "dirt";
    public static final String $6 = "fertilize_area";
    public static final String $7 = "gold_pick_diggable";
    public static final String $8 = "grass";
    public static final String $9 = "gravel";
    public static final String $10 = "iron_pick_diggable";
    public static final String $11 = "jungle";
    public static final String $12 = "log";
    public static final String $13 = "metal";
    public static final String $14 = "minecraft:crop";
    public static final String $15 = "mob_spawner";
    public static final String $16 = "not_feature_replaceable";
    public static final String $17 = "oak";
    public static final String $18 = "plant";
    public static final String $19 = "pumpkin";
    public static final String $20 = "rail";
    public static final String $21 = "sand";
    public static final String $22 = "snow";
    public static final String $23 = "spruce";
    public static final String $24 = "stone";
    public static final String $25 = "stone_pick_diggable";
    public static final String $26 = "text_sign";
    public static final String $27 = "water";
    public static final String $28 = "wood";
    public static final String $29 = "wood_pick_diggable";

    //PNX only
    public static final String $30 = "pnx:wool";
    public static final String $31 = "pnx:shulkerbox";


    private static final Object2ObjectOpenHashMap<String, Set<String>> TAG_2_BLOCKS = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Set<String>> BLOCKS_2_TAGS = new Object2ObjectOpenHashMap<>();

    static {
        try {
            try (var $32 = Server.class.getClassLoader().getResourceAsStream("block_tags.json")) {
                TypeToken<HashMap<String, HashSet<String>>> typeToken = new TypeToken<>() {
                };
                assert stream != null;
                HashMap<String, HashSet<String>> map = JSONUtils.from(new InputStreamReader(stream), typeToken);
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
    /**
     * @deprecated 
     */
    

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
    /**
     * @deprecated 
     */
    
    public static void register(String identifier, Collection<String> tags) {
        final var $33 = BLOCKS_2_TAGS.get(identifier);
        if (tagSet != null) tagSet.addAll(tags);
        else BLOCKS_2_TAGS.put(identifier, new HashSet<>(tags));
        for (var tag : tags) {
            var $34 = TAG_2_BLOCKS.get(tag);
            if (itemSet != null) itemSet.add(identifier);
            else TAG_2_BLOCKS.put(tag, new HashSet<>(Collections.singleton(identifier)));
        }
    }
}
