package org.powernukkitx.registry.mappings.loader;

import org.powernukkitx.registry.mappings.RegistryLoader;
import org.powernukkitx.utils.JSONUtils;
import com.google.common.collect.HashBiMap;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;

public class LevelSoundEventRegistryLoader implements RegistryLoader<String, HashBiMap<Integer, String>> {

    @Override
    public HashBiMap<Integer, String> load(String input) {
        try (var stream = new InputStreamReader(
                Objects.requireNonNull(LevelSoundEventRegistryLoader.class.getClassLoader().getResourceAsStream("mappings/level_sound_id_map.json")))
        ) {
            final Map<String, Number> entries = JSONUtils.from(
                    stream,
                    new TypeToken<>() {}
            );
            HashBiMap<Integer, String> sounds = HashBiMap.create(entries.size());
            entries.forEach((name, id) -> sounds.put(id.intValue(), name));
            return sounds;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
