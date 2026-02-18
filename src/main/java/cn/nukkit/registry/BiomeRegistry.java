package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitionData;
import org.cloudburstmc.protocol.bedrock.data.biome.BiomeDefinitions;
import org.cloudburstmc.protocol.bedrock.packet.BiomeDefinitionListPacket;
import org.jetbrains.annotations.UnmodifiableView;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BiomeRegistry implements IRegistry<Integer, BiomeDefinitionData, BiomeDefinitionData> {
    private static final ObjectArrayList<String> BIOME_STRING_LIST = new ObjectArrayList<>();
    private static final Int2ObjectOpenHashMap<BiomeDefinitionData> DEFINITIONS = new Int2ObjectOpenHashMap<>(0xFF);
    private static final Object2IntOpenHashMap<String> NAME2ID = new Object2IntOpenHashMap<>(0xFF);
    private static final AtomicBoolean IS_LOAD = new AtomicBoolean(false);

    @Override
    public void init() {
        if (IS_LOAD.getAndSet(true)) {
            return;
        }
        loadBiomeNameMap();
        loadBiomeDefinitions();
    }

    private void loadBiomeNameMap() {
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biomes.json");
             var reader = new InputStreamReader(stream)) {
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(JsonReader::nextInt).create();
            Map<String, ?> map = gson.fromJson(reader, new TypeToken<Map<String, ?>>() {
            }.getType());
            for (var e : map.entrySet()) {
                Object value = e.getValue();
                if (value instanceof Number number) {
                    NAME2ID.put(e.getKey(), number.intValue());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void loadBiomeDefinitions() {
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biome_definitions.nbt")) {
            CompoundTag root = NBTIO.readCompressed(stream);
            BIOME_STRING_LIST.addAll(root.getList("biomeStringList", StringTag.class).getAll().stream().map(tag -> tag.data).toList());
            ListTag<CompoundTag> biomeData = root.getList("biomeData", CompoundTag.class);

            for (CompoundTag biomeTag : biomeData.getAll()) {
                short index = biomeTag.getShort("index");
                String fullName = getFromBiomeStringList(index);
                int biomeId = getBiomeId(fullName);
                var tags = biomeTag.getList("tags", StringTag.class).getAll().stream().map(tag -> tag.data).toList();

                BiomeDefinitionData definition = new BiomeDefinitionData(
                        fullName,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        Color.BLUE,
                        true,
                        tags,
                        null
                );
                register(biomeId, definition);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BiomeDefinitionData get(Integer key) {
        return DEFINITIONS.get(key.intValue());
    }

    public BiomeDefinitionData get(String biomeName) {
        return get(NAME2ID.getInt(biomeName));
    }

    public String getFromBiomeStringList(int index) {
        return BIOME_STRING_LIST.get(index);
    }

    public int getBiomeId(String biomeName) {
        String key = biomeName.contains(":") ? biomeName.split(":")[1] : biomeName;
        return NAME2ID.getInt(key);
    }

    public BiomeDefinitionListPacket getBiomeDefinitionListPacket() {
        BiomeDefinitionListPacket packet = new BiomeDefinitionListPacket();
        Map<String, BiomeDefinitionData> definitions = new java.util.HashMap<>();
        for (BiomeDefinitionData definition : DEFINITIONS.values()) {
            definitions.put(definition.getId(), definition);
        }
        packet.setBiomes(new BiomeDefinitions(definitions));
        return packet;
    }

    @UnmodifiableView
    public Set<BiomeDefinitionData> getBiomeDefinitions() {
        return Collections.unmodifiableSet(new HashSet<>(DEFINITIONS.values()));
    }

    @Override
    public void trim() {
        DEFINITIONS.trim();
        NAME2ID.trim();
    }

    @Override
    public void reload() {
        IS_LOAD.set(false);
        DEFINITIONS.clear();
        NAME2ID.clear();
        BIOME_STRING_LIST.clear();
        init();
    }

    @Override
    public void register(Integer key, BiomeDefinitionData value) throws RegisterException {
        int id = key.intValue();
        if (DEFINITIONS.putIfAbsent(id, value) == null) {
            if (value.getId() != null) {
                String name = value.getId().contains(":") ? value.getId().split(":")[1] : value.getId();
                NAME2ID.put(name, id);
            }
        } else {
            throw new RegisterException("This biome has already been registered with the id: " + id);
        }
    }

    public int registerToBiomeStringList(String value) {
        BIOME_STRING_LIST.add(value);
        return BIOME_STRING_LIST.size() - 1;
    }
}
