package cn.nukkit.level.format.palette;

import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
@FunctionalInterface
public interface PersistentDataDeserializer<V> {
    V deserialize(CompoundTag nbtMap);
}
