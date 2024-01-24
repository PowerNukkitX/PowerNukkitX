package cn.nukkit.level.format.palette;

/**
 * Allay Project 2023/4/14
 *
 * @author JukeboxMC | daoge_cmd
 */
@FunctionalInterface
public interface RuntimeDataSerializer<V> {
    int serialize(V value);
}
