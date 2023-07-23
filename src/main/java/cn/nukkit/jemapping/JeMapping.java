package cn.nukkit.jemapping;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.PropertyTypes;
import cn.nukkit.level.terra.handles.PNXWorldHandle;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MinecraftNamespaceComparator;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.TreeMap;

/**
 * PowerNukkitX Project 2023/7/12
 *
 * @author daoge_cmd
 */
@PowerNukkitXOnly
@Since("1.20.0-r3")
public class JeMapping {
    private static final Map<JeBlockState, Integer> JE_BLOCK_STATE_TO_BE_BLOCK_STATE_HASH_MAP = new Object2IntOpenHashMap<>();
    private static final Map<Integer, JeBlockState> BE_BLOCK_STATE_HASH_TO_JE_BLOCK_STATE_MAP = new Int2ObjectOpenHashMap<>();

    private static final Map<String, Integer> JE_TO_BE_BIOME_MAP = new Object2IntOpenHashMap<>();
    private static final Map<Integer, String> BE_TO_JE_BIOME_MAP = new Int2ObjectOpenHashMap<>();

    static {
        init();
    }

    @Nullable
    public static String getJeBiomeNameByBeBiomeId(int beBiomeId) {
        return BE_TO_JE_BIOME_MAP.get(beBiomeId);
    }

    @Nullable
    public static Integer getBeBiomeIdByJeBiomeName(String jeBiomeName) {
        return JE_TO_BE_BIOME_MAP.get(jeBiomeName);
    }

    @Nullable
    public static JeBlockState getJeBlockStateByBeBlockStateHash(int beBlockStateHash) {
        return BE_BLOCK_STATE_HASH_TO_JE_BLOCK_STATE_MAP.get(beBlockStateHash);
    }

    @Nullable
    public static Integer getBeBlockStateHashByJeBlockState(JeBlockState jeBlockState) {
        return JE_BLOCK_STATE_TO_BE_BLOCK_STATE_HASH_MAP.get(jeBlockState);
    }

    private static void init() {
        //Block
        final var jeBlocksMappingConfig = new Config(Config.JSON);
        try {
            jeBlocksMappingConfig.load(JeMapping.class.getModule().getResourceAsStream("jeMappings/jeBlocksMapping.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jeBlocksMappingConfig.getAll().forEach((k, v) -> {
            var valueMap = (Map<String, Object>) v;
            var hash = computeBedrockBlockStateHash(
                            (String) valueMap.get("bedrock_identifier"),
                            (Map<String, Object>) valueMap.get("bedrock_states")
            );
            BE_BLOCK_STATE_HASH_TO_JE_BLOCK_STATE_MAP.put(
                    hash,
                    new JeBlockState(k)
            );
            JE_BLOCK_STATE_TO_BE_BLOCK_STATE_HASH_MAP.put(
                    new JeBlockState(k),
                    hash
            );
        });

        //Biome
        final var jeBiomesMappingConfig = new Config(Config.JSON);
        try {
            jeBiomesMappingConfig.load(PNXWorldHandle.class.getModule().getResourceAsStream("jeMappings/jeBiomesMapping.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        jeBiomesMappingConfig.getAll().forEach((k, v) -> {
            var bedrockBiomeId = ((Map<String, Double>) v).get("bedrock_id").intValue();
            JE_TO_BE_BIOME_MAP.put(k, bedrockBiomeId);
            BE_TO_JE_BIOME_MAP.put(bedrockBiomeId, k);
        });
    }

    @SneakyThrows
    private static int computeBedrockBlockStateHash(String name, @Nullable Map<String, Object> states) {
        var tag = new CompoundTag();
        tag.putString("name", name);
        var stateTag = new CompoundTag(new TreeMap<>());
        if (states != null) {
            for (var entry : states.entrySet()) {
                var bedrockPropertyType = PropertyTypes.getPropertyTypeString(entry.getKey());
                switch (bedrockPropertyType) {
                    case "INTEGER" -> {
                        stateTag.putInt(entry.getKey(), ((Double) entry.getValue()).intValue());
                    }
                    case "BOOLEAN" -> {
                        stateTag.putBoolean(entry.getKey(), (Boolean) entry.getValue());
                    }
                    case "ENUM" -> {
                        stateTag.putString(entry.getKey(), (String) entry.getValue());
                    }
                }
            }
        }
        tag.putCompound("states", stateTag);
        return MinecraftNamespaceComparator.fnv1a_32(NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN));
    }
}
