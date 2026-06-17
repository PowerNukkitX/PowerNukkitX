package cn.nukkit.registry.mappings;


import cn.nukkit.registry.mappings.loader.BiomeRegistryLoader;
import cn.nukkit.registry.mappings.loader.LevelSoundEventRegistryLoader;
import cn.nukkit.registry.mappings.populator.BlockRegistryPopulator;
import com.google.common.collect.HashBiMap;

public class MappingRegistries {
    /**
     * A mapped registry which stores Java biome identifiers and their Bedrock biome identifier.
     */
    public static final SimpleMappingRegistry<HashBiMap<Integer, String>> BIOME = SimpleMappingRegistry.create(
            "mappings/biomes.json",
            BiomeRegistryLoader::new
    );

    /**
     * A mapped registry which stores the numeric {@link cn.nukkit.network.protocol.types.LevelSoundEvent} id
     * and its Bedrock string identifier. Used to translate the sound id sent over the wire.
     */
    public static final SimpleMappingRegistry<HashBiMap<Integer, String>> LEVEL_SOUND_EVENT = SimpleMappingRegistry.create(
            "mappings/level_sound_id_map.json",
            LevelSoundEventRegistryLoader::new
    );

    /**
     * A versioned registry which holds {@link BlockMappings} for each version. These block mappings contain
     * primarily Bedrock version-specific data.
     */
    public static final BlockMappings BLOCKS = BlockRegistryPopulator.initMapping();
}
