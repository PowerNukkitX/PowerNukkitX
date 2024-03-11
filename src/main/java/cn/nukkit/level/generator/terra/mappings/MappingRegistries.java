package cn.nukkit.level.generator.terra.mappings;


import cn.nukkit.level.generator.terra.mappings.loader.BiomeRegistryLoader;
import cn.nukkit.level.generator.terra.mappings.populator.BlockRegistryPopulator;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MappingRegistries {

    public static final Gson JSON_MAPPER = new GsonBuilder().create();

    /**
     * A mapped registry which stores Java biome identifiers and their Bedrock biome identifier.
     */
    public static final SimpleMappingRegistry<HashBiMap<Integer, String>> BIOME = SimpleMappingRegistry.create(
            "mappings/biomes.json",
            BiomeRegistryLoader::new
    );

    /**
     * A versioned registry which holds {@link BlockMappings} for each version. These block mappings contain
     * primarily Bedrock version-specific data.
     */
    public static final BlockMappings BLOCKS = BlockRegistryPopulator.initMapping();
}
