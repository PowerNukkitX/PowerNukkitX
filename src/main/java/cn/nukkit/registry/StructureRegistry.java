package cn.nukkit.registry;

import cn.nukkit.level.structure.AbstractStructure;
import cn.nukkit.level.structure.PNXStructure;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.SneakyThrows;
import org.apache.logging.log4j.util.InternalApi;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.util.Set;

/**
 * @apiNote This is for vanilla structures. If you want to use your own structures, you can, but do not have to use it.
 */
@InternalApi
public final class StructureRegistry implements IRegistry<String, AbstractStructure, AbstractStructure> {
    private static final Object2ObjectOpenHashMap<String, AbstractStructure> REGISTRY = new Object2ObjectOpenHashMap<>();

    @Override
    public void init() {
        try (var stream = ItemRegistry.class.getClassLoader().getResourceAsStream("gamedata/unknown/structures.nbt")) {
            CompoundTag structureTag = NBTIO.readCompressed(stream);
            registerFromTag("", structureTag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private void registerFromTag(String name, CompoundTag tag) {
        for(var entry : tag.getTags().entrySet()) {
            String name1 = entry.getKey();
            String identifier = name + (name.isEmpty() ? "" : "/") + name1;
            if(entry.getValue() instanceof CompoundTag tag1) {
                if(tag1.getTags().keySet().contains("palette")) {
                    PNXStructure structure = PNXStructure.fromNbt(tag1);
                    structure.setName(identifier);
                    this.register(structure);
                } else registerFromTag(identifier, tag1);
            }
        }
    }

    @Override
    public AbstractStructure get(String key) {
        return REGISTRY.get(key);
    }

    @UnmodifiableView
    public Set<AbstractStructure> getAllState() {
        return Set.copyOf(REGISTRY.values());
    }

    @Override
    public void trim() {
        REGISTRY.trim();
    }

    @Override
    public void reload() {
        REGISTRY.clear();
    }

    @Override
    public void register(String key, AbstractStructure value) throws RegisterException {
        if (REGISTRY.putIfAbsent(key, value) == null) {
        } else {
            throw new RegisterException("The blockstate has been registered!");
        }
    }

    public void register(AbstractStructure value) throws RegisterException {
        AbstractStructure now;
        if ((now = REGISTRY.put(value.getName(), value)) == null) {
        } else {
            throw new RegisterException("The blockstate " + value + "has been registered,\n current value: " + now);
        }
    }

    @ApiStatus.Internal
    public void registerInternal(AbstractStructure value) {
        try {
            register(value);
        } catch (RegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
