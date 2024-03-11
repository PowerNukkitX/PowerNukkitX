package cn.nukkit.level.generator.terra.mappings;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A versioned, mapped registry. Like {@link SimpleMappedRegistry}, the {@link Map} interface is
 * not able to be specified here, but unlike it, it does not have support for specialized
 * instances, and ONLY supports {@link Int2ObjectMap} for optimal performance to prevent boxing
 * of integers.
 *
 * @param <V> the value
 */
public class VersionedMappingRegistry<V> extends AbstractMappedRegistry<Integer, V, Int2ObjectMap<V>> {
    protected <I> VersionedMappingRegistry(I input, RegistryLoader<I, Int2ObjectMap<V>> registryLoader) {
        super(input, registryLoader);
    }

    /**
     * Gets the closest value for the specified version. Only
     * returns versions higher up than the specified if one
     * does not exist for the given one. Useful in the event
     * that you want to get a resource which is guaranteed for
     * older versions, but not on newer ones.
     *
     * @param version the version
     * @return the closest value for the specified version
     */
    public V forVersion(int version) {
        Int2ObjectMap.Entry<V> current = null;
        for (Int2ObjectMap.Entry<V> entry : this.mappings.int2ObjectEntrySet()) {
            int currentVersion = entry.getIntKey();
            if (version < currentVersion) {
                continue;
            }
            if (version == currentVersion) {
                return entry.getValue();
            }
            if (current == null || current.getIntKey() < currentVersion) {
                // This version is newer and should be prioritized
                current = entry;
            }
        }
        return current == null ? null : current.getValue();
    }

    /**
     * Creates a new versioned registry with the given {@link RegistryLoader}. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader
     */
    public static <I, V> VersionedMappingRegistry<V> create(RegistryLoader<I, Int2ObjectMap<V>> registryLoader) {
        return new VersionedMappingRegistry<>(null, registryLoader);
    }

    /**
     * Creates a new versioned registry with the given {@link RegistryLoader} and input.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader
     */
    public static <I, V> VersionedMappingRegistry<V> create(I input, RegistryLoader<I, Int2ObjectMap<V>> registryLoader) {
        return new VersionedMappingRegistry<>(input, registryLoader);
    }

    /**
     * Creates a new versioned registry with the given {@link RegistryLoader} supplier.
     * The input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, V> VersionedMappingRegistry< V> create(Supplier<RegistryLoader<I, Int2ObjectMap<V>>> registryLoader) {
        return new VersionedMappingRegistry<>(null, registryLoader.get());
    }

    /**
     * Creates a new versioned registry with the given {@link RegistryLoader} supplier and input.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, V> VersionedMappingRegistry< V> create(I input, Supplier<RegistryLoader<I, Int2ObjectMap<V>>> registryLoader) {
        return new VersionedMappingRegistry<>(input, registryLoader.get());
    }
}
