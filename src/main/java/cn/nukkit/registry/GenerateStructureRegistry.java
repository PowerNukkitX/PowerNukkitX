package cn.nukkit.registry;

import cn.nukkit.level.biome.BiomeID;
import cn.nukkit.level.structure.AbstractStructure;
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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateStructureRegistry implements IRegistry<Integer, Object2ObjectOpenHashMap<String, AbstractStructure>, AbstractStructure> {
    private static final Int2ObjectOpenHashMap<Object2ObjectOpenHashMap<String, AbstractStructure>> BIOME_STRUCTURES = new Int2ObjectOpenHashMap<>();
    private static final AtomicBoolean isLoad = new AtomicBoolean(false);

    @Override
    public void init() {
        if (isLoad.getAndSet(true)) return;

        CompoundTag data = null;

        try (var stream = GenerateStructureRegistry.class.getClassLoader().getResourceAsStream("gamedata/custom/structures.nbt")) {
            data = NBTIO.readCompressed(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        register(BiomeID.DESERT, "village.desert.town_centers.desert_meeting_point_1", data);
        register(BiomeID.DESERT, "village.desert.town_centers.desert_meeting_point_2", data);
        register(BiomeID.DESERT, "village.desert.town_centers.desert_meeting_point_3", data);
        data = null;
    }

    @Override
    public Object2ObjectOpenHashMap<String, AbstractStructure> get(Integer key) {
        return BIOME_STRUCTURES.get(key);
    }

    @Override
    public void trim() {
        BIOME_STRUCTURES.trim();
    }

    @Override
    public void reload() {
        isLoad.set(false);
        BIOME_STRUCTURES.clear();
        init();
    }

    @Override
    public void register(Integer key, AbstractStructure value) throws RegisterException {
        Object2ObjectOpenHashMap<String, AbstractStructure> structures = BIOME_STRUCTURES.get(key);
        if (structures == null) {
            structures = new Object2ObjectOpenHashMap<>();
        }

        if (!structures.containsKey(value.getName())) {
            structures.put(value.getName(), value);
            BIOME_STRUCTURES.put(key, structures);
        } else {
            throw new RegisterException("Structure " + value.getName() + " already registered for biome " + key);
        }
    }

    public void register(Integer key, String name, CompoundTag root) {
        String[] parts = name.split("\\.");
        CompoundTag current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            if (current.contains(parts[i]) && current.get(parts[i]) instanceof CompoundTag) {
                current = current.getCompound(parts[i]);
            } else {
                return; // path not found
            }
        }

        if (!(current.get(parts[parts.length - 1]) instanceof CompoundTag structureTag)) {
            return; // no valid structure at this path
        }

        AbstractStructure structure = AbstractStructure.fromNbt(structureTag);
        if (structure != null) {
            try {
                structure.setName(name);
                register(key, structure);
            } catch (RegisterException e) {
                e.printStackTrace();
            }
        }
    }

}
