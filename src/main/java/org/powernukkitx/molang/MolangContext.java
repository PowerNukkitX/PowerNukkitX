package org.powernukkitx.molang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supplies the values a Molang expression asks for while it is evaluated.
 * <p>
 * An expression is compiled once and evaluated against many contexts, so a context is
 * expected to be cheap to create and is not retained after evaluation.
 * <p>
 * Returning null for something the expression asks for is normal rather than an error:
 * Molang treats an unknown query or variable as {@code 0}, so a context only has to
 * answer what it actually knows about.
 */
public interface MolangContext {

    /**
     * A context that knows nothing, so every query and variable reads as {@code 0}.
     */
    MolangContext EMPTY = new MolangContext() {
        @Override
        public @Nullable Object query(@NotNull String name, @NotNull Object[] arguments) {
            return null;
        }
    };

    /**
     * Resolves a {@code query.}/{@code q.} accessor.
     *
     * @param name      the accessor name, lower-cased, without the {@code query.} prefix
     * @param arguments the evaluated arguments, empty when the accessor was written
     *                  without a call
     * @return the value, or null when this context does not know the accessor, which
     * evaluates as {@code 0}
     */
    @Nullable Object query(@NotNull String name, @NotNull Object[] arguments);

    /**
     * Resolves a {@code variable.}/{@code v.} accessor.
     *
     * @param name the variable name, lower-cased, without the {@code variable.} prefix
     * @return the value, or null when unset, which evaluates as {@code 0}
     */
    default @Nullable Object variable(@NotNull String name) {
        return null;
    }

    /**
     * Stores a {@code variable.}/{@code v.} accessor. The default implementation discards
     * the value, which is what a read-only context wants.
     *
     * @param name  the variable name, lower-cased, without the {@code variable.} prefix
     * @param value the value to store
     */
    default void setVariable(@NotNull String name, @Nullable Object value) {
    }
}
