package cn.nukkit.level.generator.terra.mappings;


import java.util.function.Supplier;

/**
 * A simple registry with no defined mapping or input type. Designed to allow
 * for simple registrations of any given type without restrictions on what
 * the input or output can be.
 *
 * @param <M> the value being held by the registry
 */
public class SimpleMappingRegistry<M> extends MappingRegistry<M> {
    private <I> SimpleMappingRegistry(I input, RegistryLoader<I, M> registryLoader) {
        super(input, registryLoader);
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader} supplier. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader supplier
     * @param <I> the input type
     * @param <M> the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> SimpleMappingRegistry<M> create(Supplier<RegistryLoader<I, M>> registryLoader) {
        return new SimpleMappingRegistry<>(null, registryLoader.get());
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader} supplier
     * and input.
     *
     * @param input the input
     * @param registryLoader the registry loader supplier
     * @param <I> the input type
     * @param <M> the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> SimpleMappingRegistry<M> create(I input, Supplier<RegistryLoader<I, M>> registryLoader) {
        return new SimpleMappingRegistry<>(input, registryLoader.get());
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader}. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <I> the input type
     * @param <M> the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> SimpleMappingRegistry<M> create(RegistryLoader<I, M> registryLoader) {
        return new SimpleMappingRegistry<>(null, registryLoader);
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader} and input.
     *
     * @param input the input
     * @param registryLoader the registry loader
     * @param <I> the input type
     * @param <M> the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> SimpleMappingRegistry<M> create(I input, RegistryLoader<I, M> registryLoader) {
        return new SimpleMappingRegistry<>(input, registryLoader);
    }
}
