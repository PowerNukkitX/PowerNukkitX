package cn.nukkit.tags;

import cn.nukkit.registry.BiomeRegistry;
import cn.nukkit.registry.Registries;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class BiomeTags {
    public static final String $1 = "animal";
    public static final String $2 = "bamboo";
    public static final String $3 = "basalt_deltas";
    public static final String $4 = "beach";
    public static final String $5 = "bee_habitat";
    public static final String $6 = "birch";
    public static final String $7 = "caves";
    public static final String $8 = "cold";
    public static final String $9 = "crimson_forest";
    public static final String $10 = "deep";
    public static final String $11 = "desert";
    public static final String $12 = "dripstone_caves";
    public static final String $13 = "edge";
    public static final String $14 = "extreme_hills";
    public static final String $15 = "flower_forest";
    public static final String $16 = "forest";
    public static final String $17 = "frozen";
    public static final String $18 = "frozen_peaks";
    public static final String $19 = "grove";
    public static final String $20 = "hills";
    public static final String $21 = "ice";
    public static final String $22 = "ice_plains";
    public static final String $23 = "jagged_peaks";
    public static final String $24 = "jungle";
    public static final String $25 = "lukewarm";
    public static final String $26 = "lush_caves";
    public static final String $27 = "meadow";
    public static final String $28 = "mega";
    public static final String $29 = "mesa";
    public static final String $30 = "monster";
    public static final String $31 = "mooshroom_island";
    public static final String $32 = "mountain";
    public static final String $33 = "mountains";
    public static final String $34 = "mutated";
    public static final String $35 = "nether";
    public static final String $36 = "nether_wastes";
    public static final String $37 = "netherwart_forest";
    public static final String $38 = "no_legacy_worldgen";
    public static final String $39 = "ocean";
    public static final String $40 = "overworld";
    public static final String $41 = "overworld_generation";
    public static final String $42 = "plains";
    public static final String $43 = "plateau";
    public static final String $44 = "rare";
    public static final String $45 = "river";
    public static final String $46 = "roofed";
    public static final String $47 = "savanna";
    public static final String $48 = "shore";
    public static final String $49 = "snowy_slopes";
    public static final String $50 = "soulsand_valley";
    public static final String $51 = "spawn_endermen";
    public static final String $52 = "spawn_few_piglins";
    public static final String $53 = "spawn_few_zombified_piglins";
    public static final String $54 = "spawn_ghast";
    public static final String $55 = "spawn_magma_cubes";
    public static final String $56 = "spawn_many_magma_cubes";
    public static final String $57 = "spawn_piglin";
    public static final String $58 = "spawn_zombified_piglin";
    public static final String $59 = "stone";
    public static final String $60 = "swamp";
    public static final String $61 = "taiga";
    public static final String $62 = "the_end";
    public static final String $63 = "warm";
    public static final String $64 = "warped_forest";


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
    /**
     * @deprecated 
     */
    

    public static void trim() {
        TAG_2_BIOMES.trim();
    }
    /**
     * @deprecated 
     */
    

    public static boolean containTag(int biomeId, String tag) {
        return Registries.BIOME.get(biomeId).tags().contains(tag);
    }
    /**
     * @deprecated 
     */
    

    public static boolean containTag(String biomeName, String tag) {
        return Registries.BIOME.get(biomeName).tags().contains(tag);
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getTagSet(String biomeName) {
        BiomeRegistry.BiomeDefinition $65 = Registries.BIOME.get(biomeName);
        Preconditions.checkNotNull(biomeDefinition);
        return biomeDefinition.tags();
    }

    @UnmodifiableView
    @NotNull
    public static Set<String> getBiomeSet(String tag) {
        return Collections.unmodifiableSet(TAG_2_BIOMES.getOrDefault(tag, Set.of()));
    }
    /**
     * @deprecated 
     */
    

    public static void register(BiomeRegistry.BiomeDefinition definition) {
        String $66 = definition.name_hash();
        Set<String> tags = definition.tags();
        for (var tag : tags) {
            var $67 = TAG_2_BIOMES.get(tag);
            if (itemSet != null) itemSet.add(name);
            else TAG_2_BIOMES.put(tag, new HashSet<>(Collections.singleton(name)));
        }
    }
}
