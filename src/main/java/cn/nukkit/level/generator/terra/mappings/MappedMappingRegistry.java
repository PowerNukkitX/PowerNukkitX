package cn.nukkit.level.generator.terra.mappings;


import java.util.Map;
import java.util.function.Supplier;

/**
 * An public registry holding a map of various registrations as defined by {@link M}.
 * The M represents the map class, which can be anything that extends {@link Map}. The
 * {@link K} and {@link V} generics are the key and value respectively.
 *
 * @param <K> the key
 * @param <V> the value
 * @param <M> the map
 */
public class MappedMappingRegistry<K, V, M extends Map<K, V>> extends AbstractMappedRegistry<K, V, M> {
    protected <I> MappedMappingRegistry(I input, RegistryLoader<I, M> registryLoader) {
        super(input, registryLoader);
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader}. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @param <M> the returned mappings type, a map in this case
     * @return a new registry with the given RegistryLoader
     */
    public static <I, K, V, M extends Map<K, V>> MappedMappingRegistry<K, V, M> create(RegistryLoader<I, M> registryLoader) {
        return new MappedMappingRegistry<>(null, registryLoader);
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader} and input.
     *
     * @param input the input
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @param <M> the returned mappings type, a map in this case
     * @return a new registry with the given RegistryLoader
     */
    public static <I, K, V, M extends Map<K, V>> MappedMappingRegistry<K, V, M> create(I input, RegistryLoader<I, M> registryLoader) {
        return new MappedMappingRegistry<>(input, registryLoader);
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader} supplier.
     * The input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader supplier
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @param <M> the returned mappings type, a map in this case
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, K, V, M extends Map<K, V>> MappedMappingRegistry<K, V, M> create(Supplier<RegistryLoader<I, M>> registryLoader) {
        return new MappedMappingRegistry<>(null, registryLoader.get());
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader} and input.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @param <M> the returned mappings type, a map in this case
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, K, V, M extends Map<K, V>> MappedMappingRegistry<K, V, M> create(I input, Supplier<RegistryLoader<I, M>> registryLoader) {
        return new MappedMappingRegistry<>(input, registryLoader.get());
    }
}
