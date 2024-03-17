package cn.nukkit.level.generator.terra.mappings;

import java.util.function.Supplier;

/**
 * A simple registry with no defined mapping or input type. Designed to allow
 * for simple registrations of any given type without restrictions on what
 * the input or output can be.
 * <p>
 * Allay Project 2023/3/18
 *
 * @param <CONTENT> the value being held by the registry
 * @author GeyserMC | daoge_cmd
 */
public class SimpleRegistry<CONTENT> implements Registry<CONTENT> {

    protected CONTENT content;

    protected <INPUT> SimpleRegistry(INPUT input, RegistryLoader<INPUT, CONTENT> registryLoader) {
        this.content = registryLoader.load(input);
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader} supplier. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader supplier
     * @param <INPUT>        the input type
     * @param <CONTENT>      the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <INPUT, CONTENT> SimpleRegistry<CONTENT> of(Supplier<RegistryLoader<INPUT, CONTENT>> registryLoader) {
        return new SimpleRegistry<>(null, registryLoader.get());
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader} supplier
     * and input.
     *
     * @param input          the input
     * @param registryLoader the registry loader supplier
     * @param <INPUT>        the input type
     * @param <CONTENT>      the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <INPUT, CONTENT> SimpleRegistry<CONTENT> of(INPUT input, Supplier<RegistryLoader<INPUT, CONTENT>> registryLoader) {
        return new SimpleRegistry<>(input, registryLoader.get());
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader}. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <INPUT>        the input type
     * @param <CONTENT>      the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <INPUT, CONTENT> SimpleRegistry<CONTENT> of(RegistryLoader<INPUT, CONTENT> registryLoader) {
        return new SimpleRegistry<>(null, registryLoader);
    }

    /**
     * Creates a new registry with the given {@link RegistryLoader} and input.
     *
     * @param input          the input
     * @param registryLoader the registry loader
     * @param <INPUT>        the input type
     * @param <CONTENT>      the returned mappings type
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <INPUT, CONTENT> SimpleRegistry<CONTENT> of(INPUT input, RegistryLoader<INPUT, CONTENT> registryLoader) {
        return new SimpleRegistry<>(input, registryLoader);
    }

    @Override
    public CONTENT getContent() {
        return content;
    }

    @Override
    public void setContent(CONTENT content) {
        this.content = content;
    }
}
