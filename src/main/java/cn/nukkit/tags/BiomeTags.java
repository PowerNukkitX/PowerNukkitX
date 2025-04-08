package cn.nukkit.tags;

import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class BiomeTags {
    public static final String ANIMAL = "animal";
    public static final String BAMBOO = "bamboo";
    public static final String BASALT_DELTAS = "basalt_deltas";
    public static final String BEACH = "beach";
    public static final String BEE_HABITAT = "bee_habitat";
    public static final String BIRCH = "birch";
    public static final String CAVES = "caves";
    public static final String CHERRY_GROVE = "cherry_grove";
    public static final String COLD = "cold";
    public static final String CRIMSON_FOREST = "crimson_forest";
    public static final String DEEP_DOWN = "deep_down";
    public static final String DESERT = "desert";
    public static final String DRIPSTONE_CAVES = "dripstone_caves";
    public static final String EDGE = "edge";
    public static final String EXTREME_HILLS = "extreme_hills";
    public static final String FLOWER_FOREST = "flower_forest";
    public static final String FOREST = "forest";
    public static final String FROZEN = "frozen";
    public static final String FROZEN_PEAKS = "frozen_peaks";
    public static final String GROVE = "grove";
    public static final String HILLS = "hills";
    public static final String ICE = "ice";
    public static final String ICE_PLAINS = "ice_plains";
    public static final String JAGGED_PEAKS = "jagged_peaks";
    public static final String JUNGLE = "jungle";
    public static final String LUKEWARM = "lukewarm";
    public static final String LUSH_CAVES = "lush_caves";
    public static final String MANGROVE_SWAMP = "mangrove_swamp";
    public static final String MEADOW = "meadow";
    public static final String MEGA = "mega";
    public static final String MESA = "mesa";
    public static final String MONSTER = "monster";
    public static final String MOOSHROOM_ISLAND = "mooshroom_island";
    public static final String MOUNTAIN = "mountain";
    public static final String MOUNTAINS = "mountains";
    public static final String MUTATED = "mutated";
    public static final String NETHER = "nether";
    public static final String NETHER_WASTES = "nether_wastes";
    public static final String NETHERWART_FOREST = "netherwart_forest";
    public static final String NO_LEGACY_WORLDOGEN = "no_legacy_worldgen";
    public static final String OCEAN = "ocean";
    public static final String OVERWORLD = "overworld";
    public static final String OVERWORLD_GENERATION = "overworld_generation";
    public static final String PLAINS = "plains";
    public static final String PLATEAU = "plateau";
    public static final String RARE = "rare";
    public static final String RIVER = "river";
    public static final String ROOFED = "roofed";
    public static final String SAVANNA = "savanna";
    public static final String SHORE = "shore";
    public static final String SNOWY_SLOPES = "snowy_slopes";
    public static final String SOULSAND_VALLEY = "soulsand_valley";
    public static final String SPAWN_ENDERMEN = "spawn_endermen";
    public static final String SPAWN_FEW_PIGLINS = "spawn_few_piglins";
    public static final String SPAWN_FEW_ZOMBIFIED_PIGLINS = "spawn_few_zombified_piglins";
    public static final String SPAWN_GHAST = "spawn_ghast";
    public static final String SPAWN_MAGMA_CUBES = "spawn_magma_cubes";
    public static final String SPAWN_MANY_MAGMA_CUBES = "spawn_many_magma_cubes";
    public static final String SPAWN_PIGLIN = "spawn_piglin";
    public static final String SPAWN_ZOMBIFIED_PIGLIN = "spawn_zombified_piglin";
    public static final String STONE = "stone";
    public static final String SUNFLOWER_PLAINS = "sunflower_plains";
    public static final String SWAMP = "swamp";
    public static final String TAIGA = "taiga";
    public static final String THE_END = "the_end";
    public static final String WARM = "warm";
    public static final String WARPED_FOREST = "warped_forest";


    private static final Object2ObjectOpenHashMap<String, Set<String>> TAG_2_BIOMES = new Object2ObjectOpenHashMap<>();

    static {
        Set<BiomeRegistry.BiomeDefinition> biomeDefinitions = Registries.BIOME.getBiomeDefinitions();
        HashMap<String, Set<String>> tmpMap = new HashMap<>();
        for (var biome : biomeDefinitions) {
            tmpMap.put(biome.name_hash(), new HashSet<>(biome.tags()));
        }
        for (var e : tmpMap.entrySet()) {
            for (var biomeTag : e.getValue()) {
                Set<String> tags = TAG_2_BIOMES.computeIfAbsent(biomeTag, (k) -> new HashSet<>());
                tags.add(e.getKey());
            }
        }
    }

    public static void trim() {
        TAG_2_BIOMES.trim();
    }

    public static boolean containTag(int biomeId, String tag) {
        return Registries.BIOME.get(biomeId).tags().contains(tag);
    }

    public static boolean containTag(String biomeName, String tag) {
        return Registries.BIOME.get(biomeName).tags().contains(tag);
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getTagSet(String biomeName) {
        BiomeRegistry.BiomeDefinition biomeDefinition = Registries.BIOME.get(biomeName);
        Preconditions.checkNotNull(biomeDefinition);
        return biomeDefinition.tags();
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getBiomeSet(String tag) {
        return Collections.unmodifiableSet(TAG_2_BIOMES.getOrDefault(tag, Set.of()));
    }

    public static void register(BiomeRegistry.BiomeDefinition definition) {
        String name = definition.name_hash();
        Set<String> tags = definition.tags();
        for (var tag : tags) {
            var itemSet = TAG_2_BIOMES.get(tag);
            if (itemSet != null) itemSet.add(name);
            else TAG_2_BIOMES.put(tag, new HashSet<>(Collections.singleton(name)));
        }
    }
}
