package org.powernukkitx.block.customblock;

import org.powernukkitx.item.customitem.ItemBuilder;
import org.powernukkitx.item.customitem.ItemRegistrationListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event bus for block and item registration events.
 * <p>
 * Provides centralized registration/unregistration callbacks for
 * server plugins and addons to react to custom block/item lifecycle.
 */
public class RegistrationEvents {
    private static final List<BlockRegistrationListener> blockListeners = new CopyOnWriteArrayList<>();
    private static final List<ItemRegistrationListener> itemListeners = new CopyOnWriteArrayList<>();

    // ---- Block events ----

    public static void addBlockListener(@NotNull BlockRegistrationListener listener) {
        blockListeners.add(listener);
    }

    public static void removeBlockListener(@NotNull BlockRegistrationListener listener) {
        blockListeners.remove(listener);
    }

    public static void fireBlockRegistering(@NotNull String identifier, @NotNull CustomBlockDefinition definition) {
        for (BlockRegistrationListener listener : blockListeners) {
            listener.onBlockRegistering(identifier, definition);
        }
    }

    public static void fireBlockRegistered(@NotNull String identifier, @NotNull CustomBlockDefinition definition) {
        for (BlockRegistrationListener listener : blockListeners) {
            listener.onBlockRegistered(identifier, definition);
        }
    }

    public static void fireBlockUnregistering(@NotNull String identifier, @NotNull CustomBlockDefinition definition) {
        for (BlockRegistrationListener listener : blockListeners) {
            listener.onBlockUnregistering(identifier, definition);
        }
    }

    // ---- Item events ----

    public static void addItemListener(@NotNull ItemRegistrationListener listener) {
        itemListeners.add(listener);
    }

    public static void removeItemListener(@NotNull ItemRegistrationListener listener) {
        itemListeners.remove(listener);
    }

    public static void fireItemRegistering(@NotNull String identifier, @NotNull ItemBuilder.ItemDefinition definition) {
        for (ItemRegistrationListener listener : itemListeners) {
            listener.onItemRegistering(identifier, definition);
        }
    }

    public static void fireItemRegistered(@NotNull String identifier, @NotNull ItemBuilder.ItemDefinition definition) {
        for (ItemRegistrationListener listener : itemListeners) {
            listener.onItemRegistered(identifier, definition);
        }
    }

    public static void fireItemUnregistering(@NotNull String identifier, @NotNull ItemBuilder.ItemDefinition definition) {
        for (ItemRegistrationListener listener : itemListeners) {
            listener.onItemUnregistering(identifier, definition);
        }
    }

    /**
     * Clear all listeners (call on server shutdown).
     */
    public static void clear() {
        blockListeners.clear();
        itemListeners.clear();
    }
}