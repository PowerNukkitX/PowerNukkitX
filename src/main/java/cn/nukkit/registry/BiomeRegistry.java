package cn.nukkit.registry;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.nbt.tag.TreeMapCompoundTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeRegistry implements IRegistry<Integer, BiomeRegistry.BiomeDefinition, BiomeRegistry.BiomeDefinition> {
    private static final Int2ObjectOpenHashMap<BiomeDefinition> DEFINITIONS = new Int2ObjectOpenHashMap<>(0xFF);
    private static final Object2IntOpenHashMap<String> NAME2ID = new Object2IntOpenHashMap<>(0xFF);
    private static final List<CompoundTag> REGISTRY = new ArrayList<>(0xFF);


    @Override
    public void init() {
        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("biome_id_and_type.json")) {
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(JsonReader::nextInt).create();
            Map<String, ?> map = gson.fromJson(new InputStreamReader(stream), Map.class);
            for (var e : map.entrySet()) {
                NAME2ID.put(e.getKey(), Integer.parseInt(((Map<String, ?>) e.getValue()).get("id").toString()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (var stream = BiomeRegistry.class.getClassLoader().getResourceAsStream("biome_definitions.nbt")) {
            TreeMapCompoundTag compoundTag = NBTIO.readCompressedTreeMapCompoundTag(stream, ByteOrder.BIG_ENDIAN);
            Map<String, Tag> tags = compoundTag.getTags();
            for (var e : tags.entrySet()) {
                int id = NAME2ID.getInt(e.getKey());
                CompoundTag value = (CompoundTag) e.getValue();
                ListTag<StringTag> tags1 = value.getList("tags", StringTag.class);
                Set<String> list = tags1.getAll().stream().map(StringTag::parseValue).collect(Collectors.toSet());
                BiomeDefinition biomeDefinition = new BiomeDefinition(
                        value.getFloat("ash"),
                        value.getFloat("blue_spores"),
                        value.getFloat("depth"),
                        value.getFloat("downfall"),
                        value.getFloat("height"),
                        value.getString("name_hash"),
                        (byte) value.getByte("rain"),
                        value.getFloat("red_spores"),
                        list,
                        value.getFloat("temperature"),
                        value.getFloat("waterColorA"),
                        value.getFloat("waterColorB"),
                        value.getFloat("waterColorG"),
                        value.getFloat("waterColorR"),
                        value.getFloat("waterTransparency"),
                        value.getFloat("white_ash")
                );
                register(id, biomeDefinition);
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

    public int getBiomeId(String biomeName) {
        return NAME2ID.getInt(biomeName);
    }

    public byte[] getBiomeDefinitionListPacketData() {
        //todo Figure out the mapping of custom biomes
        try (InputStream resourceAsStream = BiomeRegistry.class.getClassLoader().getResourceAsStream("biome_definitions_vanilla.nbt")){
            assert resourceAsStream != null;
            return resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public void register(Integer key, BiomeDefinition value) throws RegisterException {
        if (DEFINITIONS.putIfAbsent(key, value) == null) {
            NAME2ID.put(value.name_hash, key);
            REGISTRY.add(value.toNBT());
        } else {
            throw new RegisterException("This biome has already been registered with the id: " + key);
        }
    }

    public record BiomeDefinition(float ash,
                                  float blue_spores,
                                  float depth,
                                  float downfall,
                                  float height,
                                  String name_hash,
                                  byte rain,
                                  float red_spores,
                                  Set<String> tags,
                                  float temperature,
                                  float waterColorA,
                                  float waterColorB,
                                  float waterColorG,
                                  float waterColorR,
                                  float waterTransparency,
                                  float white_ash) {

        @UnmodifiableView
        @Override
        public Set<String> tags() {
            return Collections.unmodifiableSet(tags);
        }

        public CompoundTag toNBT() {
            ListTag<StringTag> stringTagListTag = new ListTag<>();
            for (var s : tags) {
                stringTagListTag.add(new StringTag( s));
            }
            return new CompoundTag()
                    .putFloat("ash", ash)
                    .putFloat("blue_spores", blue_spores)
                    .putFloat("depth", depth)
                    .putFloat("downfall", downfall)
                    .putFloat("height", height)
                    .putString("name_hash", name_hash)
                    .putByte("rain", rain)
                    .putFloat("red_spores", red_spores)
                    .putList("tags", stringTagListTag)
                    .putFloat("temperature", temperature)
                    .putFloat("waterColorA", waterColorA)
                    .putFloat("waterColorB", waterColorB)
                    .putFloat("waterColorG", waterColorG)
                    .putFloat("waterColorG", waterColorG)
                    .putFloat("waterColorR", waterColorR)
                    .putFloat("waterTransparency", waterTransparency)
                    .putFloat("white_ash", white_ash);
        }
    }
}
