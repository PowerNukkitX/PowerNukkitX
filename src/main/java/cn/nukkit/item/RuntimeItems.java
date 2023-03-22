package cn.nukkit.item;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@UtilityClass
public class RuntimeItems {

    private static final Map<String, Integer> legacyString2LegacyInt = new HashMap<>();
    private static RuntimeItemMapping itemPalette;
    private static boolean initialized;

    static {
        if (initialized) {
            throw new IllegalStateException("RuntimeItems were already generated!");
        }
        initialized = true;
        log.info("Loading runtime items...");

        try (InputStream itemIdsStream = Server.class.getClassLoader().getResourceAsStream("legacy_item_ids.json")) {
            if (itemIdsStream == null) {
                throw new AssertionError("Unable to load legacy_item_ids.json");
            }
            JsonObject json = JsonParser.parseReader(new InputStreamReader(itemIdsStream)).getAsJsonObject();
            for (String identifier : json.keySet()) {
                legacyString2LegacyInt.put(identifier, json.get(identifier).getAsInt());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, MappingEntry> mappingEntries = new HashMap<>();
        try (InputStream mappingStream = Server.class.getClassLoader().getResourceAsStream("item_mappings.json")) {
            if (mappingStream == null) {
                throw new AssertionError("Unable to load item_mappings.json");
            }
            JsonObject itemMapping = JsonParser.parseReader(new InputStreamReader(mappingStream)).getAsJsonObject();
            for (String legacyName : itemMapping.keySet()) {
                JsonObject convertData = itemMapping.getAsJsonObject(legacyName);
                for (String damageStr : convertData.keySet()) {
                    String identifier = convertData.get(damageStr).getAsString();
                    int damage = Integer.parseInt(damageStr);
                    mappingEntries.put(identifier, new MappingEntry(legacyName, damage));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        itemPalette = new RuntimeItemMapping(mappingEntries);
    }

    public static void init() {
    }

    public static RuntimeItemMapping getMapping() {
        return itemPalette;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static RuntimeItemMapping getRuntimeMapping() {
        return itemPalette;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getId(int fullId) {
        return (short) (fullId >> 16);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getData(int fullId) {
        return ((fullId >> 1) & 0x7fff);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static int getFullId(int id, int data) {
        return (((short) id) << 16) | ((data & 0x7fff) << 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean hasData(int id) {
        return getData(id) != 0;
    }

    public static int getLegacyIdFromLegacyString(String identifier) {
        return legacyString2LegacyInt.getOrDefault(identifier, -1);
    }

    public record MappingEntry(String legacyName, int damage) {
    }
}
