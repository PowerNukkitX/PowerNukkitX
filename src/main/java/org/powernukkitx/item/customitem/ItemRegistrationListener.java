package org.powernukkitx.item.customitem;

import org.jetbrains.annotations.NotNull;

/**
 * Listener interface for item registration events.
 * <p>
 * Implement this interface to receive notifications when custom items
 * are registered or unloaded from the server.
 */
public interface ItemRegistrationListener {

    /**
     * Called when a custom item is being registered.
     *
     * @param identifier the item identifier
     * @param definition the item definition being registered
     */
    default void onItemRegistering(@NotNull String identifier, @NotNull ItemBuilder.ItemDefinition definition) {}

    /**
     * Called after a custom item has been registered successfully.
     *
     * @param identifier the item identifier
     * @param definition the item definition that was registered
     */
    default void onItemRegistered(@NotNull String identifier, @NotNull ItemBuilder.ItemDefinition definition) {}

    /**
     * Called when a custom item is being unloaded/removed.
     *
     * @param identifier the item identifier
     * @param definition the item definition being unloaded
     */
    default void onItemUnregistering(@NotNull String identifier, @NotNull ItemBuilder.ItemDefinition definition) {}
}