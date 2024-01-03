package cn.nukkit.tags;

import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class BiomeTags {
    private static final Object2ObjectOpenHashMap<String, Set<String>> TAG_2_BIOMES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<String, Set<String>> BIOMES_2_TAGS = new Object2ObjectOpenHashMap<>();

    static {
        Set<BiomeRegistry.BiomeDefinition> biomeDefinitions = Registries.BIOME.getBiomeDefinitions();
        for (var biome : biomeDefinitions) {
            BIOMES_2_TAGS.put(biome.name_hash(), new HashSet<>(biome.tags()));
        }
        for (var e : BIOMES_2_TAGS.entrySet()) {
            for (var biomeTag : e.getValue()) {
                Set<String> tags = TAG_2_BIOMES.computeIfAbsent(biomeTag, (k) -> new HashSet<>());
                tags.add(e.getKey());
            }
        }
    }

    public static void trim() {
        TAG_2_BIOMES.trim();
        BIOMES_2_TAGS.trim();
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getTagSet(String biomeName) {
        return Collections.unmodifiableSet(BIOMES_2_TAGS.getOrDefault(biomeName, Set.of()));
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getBiomeSet(String tag) {
        return Collections.unmodifiableSet(TAG_2_BIOMES.getOrDefault(tag, Set.of()));
    }

    public static void register(BiomeRegistry.BiomeDefinition definition) {
        String name = definition.name_hash();
        Set<String> tags = definition.tags();
        var tagSet = BIOMES_2_TAGS.get(name);
        if (tagSet != null) tagSet.addAll(tags);
        else BIOMES_2_TAGS.put(name, new HashSet<>(tags));
        for (var tag : tags) {
            var itemSet = TAG_2_BIOMES.get(tag);
            if (itemSet != null) itemSet.add(name);
            else TAG_2_BIOMES.put(tag, new HashSet<>(Collections.singleton(name)));
        }
    }
}
