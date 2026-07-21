package org.powernukkitx.block.customblock;

import org.jetbrains.annotations.NotNull;

/**
 * Listener interface for block registration events.
 * <p>
 * Implement this interface to receive notifications when custom blocks
 * are registered or unloaded from the server.
 */
public interface BlockRegistrationListener {

    /**
     * Called when a custom block is being registered.
     *
     * @param identifier the block identifier
     * @param definition the block definition being registered
     */
    default void onBlockRegistering(@NotNull String identifier, @NotNull CustomBlockDefinition definition) {}

    /**
     * Called after a custom block has been registered successfully.
     *
     * @param identifier the block identifier
     * @param definition the block definition that was registered
     */
    default void onBlockRegistered(@NotNull String identifier, @NotNull CustomBlockDefinition definition) {}

    /**
     * Called when a custom block is being unloaded/removed.
     *
     * @param identifier the block identifier
     * @param definition the block definition being unloaded
     */
    default void onBlockUnregistering(@NotNull String identifier, @NotNull CustomBlockDefinition definition) {}
}