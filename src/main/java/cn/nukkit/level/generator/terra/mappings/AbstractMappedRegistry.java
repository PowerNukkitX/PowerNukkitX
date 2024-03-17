package cn.nukkit.level.generator.terra.mappings;


import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * An abstract registry holding a map of various registrations as defined by {@link M}.
 * The M represents the map class, which can be anything that extends {@link Map}. The
 * {@link K} and {@link V} generics are the key and value respectively.
 *
 * @param <K> the key
 * @param <V> the value
 * @param <M> the map
 */
public abstract class AbstractMappedRegistry<K, V, M extends Map<K, V>> extends MappingRegistry<M> {

    protected <I> AbstractMappedRegistry(I input, RegistryLoader<I, M> registryLoader) {
        super(input, registryLoader);
    }

    /**
     * Returns the value registered by the given key.
     *
     * @param key the key
     * @return the value registered by the given key.
     */
    @Nullable
    public V get(K key) {
        return this.mappings.get(key);
    }

    /**
     * Returns and maps the value by the given key if present.
     *
     * @param key    the key
     * @param mapper the mapper
     * @param <U>    the type
     * @return the mapped value from the given key if present
     */
    public <U> Optional<U> map(K key, Function<? super V, ? extends U> mapper) {
        V value = this.get(key);
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
     *         specified if null.
     */
    public V getOrDefault(K key, V defaultValue) {
        return this.mappings.getOrDefault(key, defaultValue);
    }

    /**
     * Registers a new value into this registry with the given key.
     *
     * @param key   the key
     * @param value the value
     * @return a new value into this registry with the given key.
     */
    public V register(K key, V value) {
        return this.mappings.put(key, value);
    }

}
