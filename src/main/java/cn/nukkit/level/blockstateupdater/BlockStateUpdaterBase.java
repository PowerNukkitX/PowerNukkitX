package cn.nukkit.level.blockstateupdater;

import cn.nukkit.level.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdaterBase implements BlockStateUpdater {
    public static final BlockStateUpdater INSTANCE = new BlockStateUpdaterBase();

    public static final Map<String, Map<String, Object>[]> LEGACY_BLOCK_DATA_MAP = new HashMap<>();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        JsonNode node;
        try (InputStream stream = BlockStateUpdater.class.getClassLoader().getResourceAsStream("legacy_block_data_map.json")) {
            node = JSON_MAPPER.readTree(stream);
        } catch (IOException e) {
            throw new AssertionError("Error loading legacy block data map", e);
        }

        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            String name = entry.getKey();
            JsonNode stateNodes = entry.getValue();

            int size = stateNodes.size();
            Map<String, Object>[] states = new Map[size];
            for (int i = 0; i < size; i++) {
                states[i] = convertStateToCompound(stateNodes.get(i));
            }

            LEGACY_BLOCK_DATA_MAP.put(name, states);
        }
    }

    private static Map<String, Object> convertStateToCompound(JsonNode node) {
        Map<String, Object> tag = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            String name = entry.getKey();
            JsonNode value = entry.getValue();
            switch (value.getNodeType()) {
                case BOOLEAN:
                    tag.put(name, value.booleanValue());
                    break;
                case NUMBER:
                    tag.put(name, value.intValue());
                    break;
                case STRING:
                    tag.put(name, value.textValue());
                    break;
                default:
                    throw new UnsupportedOperationException("Invalid state type");
            }
        }
        return tag;
    }

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext context) {
        context.addUpdater(0, 0, 0)
                .regex("name", "minecraft:.+")
                .regex("val", "[0-9]+")
                .addCompound("states")
                .tryEdit("states", helper -> {
                    Map<String, Object> tag = helper.getCompoundTag();
                    Map<String, Object> parent = helper.getParent();
                    String id = (String) parent.get("name");
                    short val = (short) parent.get("val");
                    Map<String, Object>[] statesArray = LEGACY_BLOCK_DATA_MAP.get(id);
                    if (statesArray != null) {
                        if (val >= statesArray.length) val = 0;
                        tag.putAll(statesArray[val]);
                    }
                })
                .remove("val");
    }
}
