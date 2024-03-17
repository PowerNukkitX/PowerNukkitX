package cn.nukkit.level.generator.terra.mappings;


import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * An array registry that stores mappings as an array defined by {@link M}.
 * The M represents the value that is to be stored as part of this array.
 *
 * @param <M> the mapping type
 */
public class ArrayMappingRegistry<M> extends MappingRegistry<M[]> {

    /**
     * Creates a new array registry of this class with the given input and
     * {@link RegistryLoader}. The input specified is what the registry
     * loader needs to take in.
     *
     * @param input          the input
     * @param registryLoader the registry loader
     */
    protected <I> ArrayMappingRegistry(I input, RegistryLoader<I, M[]> registryLoader) {
        super(input, registryLoader);
    }

    /**
     * Returns the value registered by the given index.
     *
     * @param index the index
     * @return the value registered by the given index.
     */
    @Nullable
    public M get(int index) {
        if (index >= this.mappings.length) {
            return null;
        }

        return this.mappings[index];
    }

    /**
     * Returns the value registered by the given index or the default value
     * specified if null.
     *
     * @param index        the index
     * @param defaultValue the default value
     * @return the value registered by the given key or the default value
     *         specified if null.
     */
    public M getOrDefault(int index, M defaultValue) {
        M value = this.get(index);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    /**
     * Registers a new value into this registry with the given index.
     *
     * @param index the index
     * @param value the value
     * @return a new value into this registry with the given index.
     */
    public M register(int index, M value) {
        return this.mappings[index] = value;
    }

    /**
     * Creates a new array registry with the given {@link RegistryLoader} supplier. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader supplier
     * @param <I>            the input type
     * @param <M>            the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> ArrayMappingRegistry<M> create(Supplier<RegistryLoader<I, M[]>> registryLoader) {
        return new ArrayMappingRegistry<>(null, registryLoader.get());
    }

    /**
     * Creates a new array registry with the given {@link RegistryLoader} supplier
     * and input.
     *
     * @param input          the input
     * @param registryLoader the registry loader supplier
     * @param <I>            the input type
     * @param <M>            the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> ArrayMappingRegistry<M> create(I input, Supplier<RegistryLoader<I, M[]>> registryLoader) {
        return new ArrayMappingRegistry<>(input, registryLoader.get());
    }

    /**
     * Creates a new array registry with the given {@link RegistryLoader}. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <I>            the input type
     * @param <M>            the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> ArrayMappingRegistry<M> create(RegistryLoader<I, M[]> registryLoader) {
        return new ArrayMappingRegistry<>(null, registryLoader);
    }

    /**
     * Creates a new array registry with the given {@link RegistryLoader} and input.
     *
     * @param input          the input
     * @param registryLoader the registry loader
     * @param <I>            the input type
     * @param <M>            the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, M> ArrayMappingRegistry<M> create(I input, RegistryLoader<I, M[]> registryLoader) {
        return new ArrayMappingRegistry<>(input, registryLoader);
    }

}
