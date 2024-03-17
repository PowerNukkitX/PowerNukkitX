package cn.nukkit.level.generator.terra.mappings;

import java.util.function.Consumer;

/**
 * Represents a registry.
 *
 * @param <M> the value being held by the registry
 */
interface IRegistry<M> {

    /**
     * Gets the underlying value held by this registry.
     *
     * @return the underlying value held by this registry.
     */
    M get();

    /**
     * Sets the underlying value held by this registry.
     * Clears any existing data associated with the previous
     * value.
     *
     * @param mappings the underlying value held by this registry
     */
    void set(M mappings);

    /**
     * Registers what is specified in the given
     * {@link Consumer} into the underlying value.
     *
     * @param consumer the consumer
     */
    void register(Consumer<M> consumer);

}
