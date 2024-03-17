package cn.nukkit.level.generator.terra.mappings;

import java.util.Map;

/**
 * Allay Project 2023/10/27
 *
 * @author daoge_cmd
 */
public interface DoubleKeyMappedRegistry<K1, K2, VALUE> extends Registry<DoubleKeyMappedRegistry.MapPair<K1, K2, VALUE>> {

    record MapPair<K1, K2, VALUE>(Map<K1, VALUE> m1, Map<K2, VALUE> m2) {

    }

    default VALUE getByK1(K1 k1) {
        return getContent().m1.get(k1);
    }

    default VALUE getByK2(K2 k2) {
        return getContent().m2.get(k2);
    }

    default VALUE getByK1OrDefault(K1 k1, VALUE defaultValue) {
        return getContent().m1.getOrDefault(k1, defaultValue);
    }

    default VALUE getByK2OrDefault(K2 k2, VALUE defaultValue) {
        return getContent().m2.getOrDefault(k2, defaultValue);
    }

    default void register(K1 k1, K2 k2, VALUE value) {
        var content = getContent();
        content.m1.put(k1, value);
        content.m2.put(k2, value);
    }

}
