package org.powernukkitx.registry.mappings;


import org.powernukkitx.registry.mappings.loader.BiomeRegistryLoader;
import org.powernukkitx.registry.mappings.loader.LevelSoundEventRegistryLoader;
import org.powernukkitx.registry.mappings.populator.BlockRegistryPopulator;
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
     * A mapped registry which stores the numeric {@link org.powernukkitx.network.protocol.types.LevelSoundEvent} id
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
