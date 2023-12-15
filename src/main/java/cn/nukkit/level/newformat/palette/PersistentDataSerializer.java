package cn.nukkit.level.newformat.palette;

import org.cloudburstmc.nbt.NbtMap;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
@FunctionalInterface
public interface PersistentDataSerializer<V> {
    NbtMap serialize(V value);
}
