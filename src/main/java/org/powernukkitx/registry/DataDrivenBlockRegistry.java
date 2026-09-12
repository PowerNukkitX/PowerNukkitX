package org.powernukkitx.registry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.data.ServerBlockProperty;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public final class DataDrivenBlockRegistry {
    private final List<ServerBlockProperty> properties = new ObjectArrayList<>();

    public void init() {
        try (var stream = DataDrivenBlockRegistry.class.getClassLoader().getResourceAsStream("gamedata/kaooot/block_definitions.nbt");
             var nbtInputStream = NbtUtils.createGZIPReader(stream)) {
            final NbtMap root = (NbtMap) nbtInputStream.readTag();
            for (NbtMap property : root.getList("properties", NbtType.COMPOUND)) {
                properties.add(new ServerBlockProperty(property.getString("name"), property.getCompound("properties")));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @UnmodifiableView
    public List<ServerBlockProperty> getProperties() {
        return List.copyOf(properties);
    }
}
