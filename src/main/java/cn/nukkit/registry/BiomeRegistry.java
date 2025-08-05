package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.BiomeDefinitionListPacket;
import cn.nukkit.network.protocol.types.biome.BiomeDefinition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class BiomeRegistry implements IRegistry<Integer, BiomeDefinition, BiomeDefinition> {
    private static final ObjectArrayList<String> BIOME_STRING_LIST = new ObjectArrayList<>();
    private static final Int2ObjectOpenHashMap<BiomeDefinition> DEFINITIONS = new Int2ObjectOpenHashMap<>(0xFF);
    private static final Object2IntOpenHashMap<String> NAME2ID = new Object2IntOpenHashMap<>(0xFF);
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biomes.json")) { //From Endstone Data
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(JsonReader::nextInt).create();
            Map<String, ?> map = gson.fromJson(new InputStreamReader(stream), Map.class);
            for (var e : map.entrySet()) {
                NAME2ID.put(e.getKey(), (Integer) e.getValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/biome_definitions.nbt")) {
            CompoundTag root = NBTIO.readCompressed(stream);
            BIOME_STRING_LIST.addAll(root.getList("biomeStringList", StringTag.class).getAll().stream().map(tag -> tag.data).toList());
            ListTag<CompoundTag> biomeData = root.getList("biomeData", CompoundTag.class);

            for(CompoundTag biomeTag : biomeData.getAll()) {
                short index = biomeTag.getShort("index");
                int biomeId = getBiomeId(getFromBiomeStringList(index));
                BiomeDefinition definition = new BiomeDefinition();
                definition.parse(biomeTag);
                register(biomeId, definition);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BiomeDefinition get(Integer key) {
        return DEFINITIONS.get(key);
    }

    public BiomeDefinition get(String biomeName) {
        return get(NAME2ID.getInt(biomeName));
    }

    public String getFromBiomeStringList(int index) {
        return BIOME_STRING_LIST.get(index);
    }

    public int getBiomeId(String biomeName) {
        return NAME2ID.getInt(biomeName.split(":")[1]);
    }

    public BiomeDefinitionListPacket getBiomeDefinitionListPacket() {
        BiomeDefinitionListPacket packet = new BiomeDefinitionListPacket();
        packet.biomeStringList = BIOME_STRING_LIST.toArray(new String[0]);
        packet.biomeDefinitionData = DEFINITIONS.values().toArray(new BiomeDefinition[0]);
        return packet;
    }

    @UnmodifiableView
    public Set<BiomeDefinition> getBiomeDefinitions() {
        return Collections.unmodifiableSet(new HashSet<>(DEFINITIONS.values()));
    }

    @Override
    public void trim() {
        DEFINITIONS.trim();
        NAME2ID.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        DEFINITIONS.clear();
        NAME2ID.clear();
        BIOME_STRING_LIST.clear();
        init();
    }

    @Override
    public void register(Integer key, BiomeDefinition value) throws RegisterException {
        if (DEFINITIONS.putIfAbsent(key, value) == null) {
            NAME2ID.put(BIOME_STRING_LIST.get(value.stringIndex), key);
        } else {
            throw new RegisterException("This biome " + value.getName() + " has already been registered with the id: " + key);
        }
    }
}
