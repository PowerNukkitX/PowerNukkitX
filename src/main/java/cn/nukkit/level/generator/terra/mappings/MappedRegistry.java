package cn.nukkit.level.generator.terra.mappings;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * An abstract registry holding a map of various registrations as defined by {@link MAPPING}.
 * The M represents the map class, which can be anything that extends {@link Map}. The
 * {@link KEY} and {@link VALUE} generics are the key and value respectively.
 *
 * @param <KEY>     the key
 * @param <VALUE>   the value
 * @param <MAPPING> the map
 *                  <p>
 * @author daoge_cmd <br>
 * Date: 2023/3/18 <br>
 * Allay Project <br>
 */
public interface MappedRegistry<KEY, VALUE, MAPPING extends Map<KEY, VALUE>> extends Registry<MAPPING> {
    /**
     * Returns the value registered by the given key.
     *
     * @param key the key
     * @return the value registered by the given key.
     */

    default VALUE get(KEY key) {
        return getContent().get(key);
    }

    /**
     * Returns and maps the value by the given key if present.
     *
     * @param key    the key
     * @param mapper the mapper
     * @param <U>    the type
     * @return the mapped value from the given key if present
     */
    default <U> Optional<U> map(KEY key, Function<? super VALUE, ? extends U> mapper) {
        VALUE value = this.get(key);
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(mapper.apply(value));
        }
    }

    /**
     * Returns the value registered by the given key or the default value
     * specified if null.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the value registered by the given key or the default value
     * specified if null.
     */
    default VALUE getOrDefault(KEY key, VALUE defaultValue) {
        return getContent().getOrDefault(key, defaultValue);
    }

    /**
     * Registers a new value into this registry with the given key.
     *
     * @param key   the key
     * @param value the value
     * @return a new value into this registry with the given key.
     */
    default VALUE register(KEY key, VALUE value) {
        return getContent().put(key, value);
    }
}
