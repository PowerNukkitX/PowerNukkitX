package org.powernukkitx.level.format.anvil;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.utils.JSONUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author iYozem
 */
@Slf4j
public final class JsonItemMapper implements JavaItemMapper {

    public static final String DEFAULT_RESOURCE = "mappings/java_legacy_item_map.json";

    private static final class Entry {
        String name;
        int damage;
    }

    private final Map<String, BedrockItem> table = new HashMap<>();

    private JsonItemMapper(Map<String, Entry> raw) {
        for (Map.Entry<String, Entry> e : raw.entrySet()) {
            if (e.getValue() != null && e.getValue().name != null) {
                this.table.put(e.getKey(), new BedrockItem(e.getValue().name, e.getValue().damage));
            }
        }
    }

    public static JsonItemMapper loadDefault() {
        try (InputStream stream = JsonItemMapper.class.getClassLoader().getResourceAsStream(DEFAULT_RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource: " + DEFAULT_RESOURCE);
            }
            Map<String, Entry> raw = JSONUtils.from(stream, new TypeToken<Map<String, Entry>>() {});
            return new JsonItemMapper(raw);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Anvil item mapping", e);
        }
    }

    @Override
    public @Nullable BedrockItem map(String javaId, int javaDamage) {
        String shortId = javaId.indexOf(':') >= 0 ? javaId.substring(javaId.indexOf(':') + 1) : javaId;
        BedrockItem item = this.table.get(shortId + ":" + javaDamage);
        if (item == null) {
            item = this.table.get(shortId);
        }
        if (item == null) {
            return null;
        }
        if (Registries.ITEM.get(item.name()) == null) {
            return null;
        }
        return item;
    }
}
