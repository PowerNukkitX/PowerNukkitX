package cn.nukkit.level.biome;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@PowerNukkitOnly("Map between legacy integer biome id and new string id")
@Since("1.6.0.0-PNX")
@Log4j2
public final class BiomeLegacyId2StringIdMap {
    public static final BiomeLegacyId2StringIdMap INSTANCE = new BiomeLegacyId2StringIdMap();

    private final Int2ObjectOpenHashMap<String> legacy2StringMap = new Int2ObjectOpenHashMap<>(82);
    private final Object2IntOpenHashMap<String> string2LegacyMap = new Object2IntOpenHashMap<>(82);

    private BiomeLegacyId2StringIdMap() {
        try (final InputStream inputStream = Server.class.getModule().getResourceAsStream("biome_id_map.json")) {
            if (inputStream == null) {
                log.warn("Cannot read biome_id_map.json. No biomes will be sent to client.");
            } else {
                final JsonObject biomeIdMap = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
                for (final Map.Entry<String, JsonElement> entry : biomeIdMap.entrySet()) {
                    final int id = entry.getValue().getAsInt();
                    legacy2StringMap.put(id, entry.getKey());
                    string2LegacyMap.put(entry.getKey(), id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String legacy2String(int legacyBiomeID) {
        return legacy2StringMap.get(legacyBiomeID);
    }

    public int string2Legacy(String stringBiomeID) {
        return string2LegacyMap.getOrDefault(stringBiomeID, -1);
    }
}
