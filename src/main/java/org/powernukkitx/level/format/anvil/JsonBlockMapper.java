package org.powernukkitx.level.format.anvil;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.JSONUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iYozem
 */
@Slf4j
public final class JsonBlockMapper implements JavaBlockMapper {
    public static final String DEFAULT_RESOURCE = "mappings/java_legacy_block_map.json";
    private static final Pattern VALUE_PATTERN = Pattern.compile("^(minecraft:[a-z0-9_]+)(?:\\[(.*)])?$");

    private final Map<Integer, BlockState> byIdMeta = new HashMap<>();
    private final Map<Integer, BlockState> byId = new HashMap<>();
    private final BlockState fallback;

    private JsonBlockMapper(Map<String, String> rawTable) {
        BlockState unknown = resolveIdentifier(BlockID.UNKNOWN);
        this.fallback = unknown != null ? unknown : Registries.BLOCK.getBlockProperties(BlockID.INFO_UPDATE).getDefaultState();

        for (Map.Entry<String, String> entry : rawTable.entrySet()) {
            String key = entry.getKey();
            BlockState state = resolveValue(entry.getValue());
            if (state == null) {
                continue;
            }
            int sep = key.indexOf(':');
            try {
                if (sep < 0) {
                    this.byId.put(Integer.parseInt(key.trim()), state);
                } else {
                    int id = Integer.parseInt(key.substring(0, sep).trim());
                    int meta = Integer.parseInt(key.substring(sep + 1).trim());
                    this.byIdMeta.put((id << 4) | (meta & 0x0F), state);
                }
            } catch (NumberFormatException e) {
                log.warn("[AnvilConverter] malformed legacy key '{}', skipping", key);
            }
        }
    }

    public static JsonBlockMapper loadDefault() {
        try (InputStream stream = JsonBlockMapper.class.getClassLoader().getResourceAsStream(DEFAULT_RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource: " + DEFAULT_RESOURCE);
            }
            Map<String, String> table = JSONUtils.from(stream, new TypeToken<Map<String, String>>() {});
            return new JsonBlockMapper(table);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Anvil block mapping", e);
        }
    }

    @Override
    public BlockState map(int javaId, int javaMeta) {
        BlockState specific = this.byIdMeta.get((javaId << 4) | (javaMeta & 0x0F));
        if (specific != null) {
            return specific;
        }
        return this.byId.get(javaId);
    }

    @Override
    public BlockState fallback() {
        return this.fallback;
    }

    private static BlockState resolveValue(String value) {
        Matcher m = VALUE_PATTERN.matcher(value.trim());
        if (!m.matches()) {
            log.warn("[AnvilConverter] malformed mapping value '{}', skipping", value);
            return null;
        }
        String identifier = m.group(1);
        BlockProperties properties = propertiesOf(identifier);
        if (properties == null) {
            log.warn("[AnvilConverter] unknown Bedrock block '{}', skipping", identifier);
            return null;
        }

        BlockState state = properties.getDefaultState();
        String states = m.group(2);
        if (states == null || states.isEmpty()) {
            return state;
        }

        Map<String, BlockPropertyType<?>> byName = new HashMap<>();
        for (BlockPropertyType<?> type : properties.getPropertyTypeSet()) {
            byName.put(type.getName(), type);
        }

        for (String pair : states.split(",")) {
            int eq = pair.indexOf('=');
            if (eq < 0) {
                continue;
            }
            String name = pair.substring(0, eq).trim();
            String raw = pair.substring(eq + 1).trim();
            BlockPropertyType<?> type = byName.get(name);
            if (type == null) {
                continue;
            }
            try {
                Object parsed = switch (type.getType()) {
                    case INT -> Integer.valueOf(Integer.parseInt(raw));
                    case BOOLEAN -> ("true".equalsIgnoreCase(raw) || "false".equalsIgnoreCase(raw))
                            ? (Object) Boolean.valueOf(Boolean.parseBoolean(raw))
                            : (Object) Integer.valueOf(Integer.parseInt(raw));
                    case ENUM -> raw;
                };
                state = state.setPropertyValue(properties, type.tryCreateValue(parsed));
            } catch (Exception e) {
                log.debug("[AnvilConverter] could not apply state {}={} to {}: {}", name, raw, identifier, e.toString());
            }
        }
        return state;
    }

    private static BlockState resolveIdentifier(String identifier) {
        BlockProperties properties = propertiesOf(identifier);
        return properties == null ? null : properties.getDefaultState();
    }

    private static BlockProperties propertiesOf(String identifier) {
        try {
            return Registries.BLOCK.getBlockProperties(identifier);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
