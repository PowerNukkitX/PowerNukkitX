package cn.nukkit.ddui;

import cn.nukkit.Player;
import cn.nukkit.ddui.properties.DataDrivenProperty;
import org.cloudburstmc.protocol.bedrock.data.ddui.DataStorePropertyType;
import org.cloudburstmc.protocol.bedrock.data.ddui.DataStoreUpdate;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundDataStorePacket;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A reactive value holder.  Subscribers are called whenever {@link #setValue}
 * is invoked, and may return a {@link DataDrivenProperty} to trigger a
 * live DataStore update for connected clients.
 *
 * @param <T> Value type — typically {@link String}, {@link Boolean}, or {@link Long}.
 * @author xRookieFight
 * @since 06/03/2026
 */
public class Observable<T> {

    /**
     * Listener contract. Return the property that changed so that a
     * {@code DataStoreUpdate} packet can be dispatched; return {@code null} when no packet is needed
     */
    @FunctionalInterface
    public interface Listener<T> {
        DataDrivenProperty<?, ?> onValue(T value);
    }

    private final Set<Listener<T>> listeners = new LinkedHashSet<>();
    private T value;
    private static final ThreadLocal<Integer> SUPPRESS_OUTBOUND = ThreadLocal.withInitial(() -> 0);

    public Observable(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * Updates the held value and notifies all subscribers.
     * For each subscriber that returns a non-null {@link DataDrivenProperty},
     * a {@code DataStoreUpdate} packet is dispatched to every viewer of the
     * owning screen.
     */
    public void setValue(T value) {
        this.value = value;

        for (Listener<T> listener : listeners) {
            DataDrivenProperty<?, ?> element = listener.onValue(value);

            if (element == null) continue;

            DataDrivenScreen screen = element.getRootScreen();
            if (screen == null) continue;

            if (SUPPRESS_OUTBOUND.get() > 0) {
                continue;
            }

            final DataStoreUpdate update = new DataStoreUpdate();

            update.setDataStoreName(screen.getIdentifier().split(":")[0]);
            update.setProperty(screen.getProperty());
            update.setPath(element.getPath());
            update.setType(
                    switch (value) {
                        case Boolean n -> DataStorePropertyType.BOOLEAN;
                        case Number n -> DataStorePropertyType.DOUBLE;
                        case String n -> DataStorePropertyType.STRING;
                        default -> throw new IllegalStateException("Unexpected value: " + value);
                    }
            );
            update.setData(value);
            update.setPropertyUpdateCount(1);
            update.setPathUpdateCount(1);

            ClientboundDataStorePacket cbDataStore = new ClientboundDataStorePacket();
            cbDataStore.getUpdates().add(update);

            for (Player viewer : screen.getAllViewers()) {
                viewer.dataPacket(cbDataStore);
            }
        }
    }

    public static void withOutboundSuppressed(Runnable runnable) {
        int depth = SUPPRESS_OUTBOUND.get();
        SUPPRESS_OUTBOUND.set(depth + 1);
        try {
            runnable.run();
        } finally {
            if (depth == 0) {
                SUPPRESS_OUTBOUND.remove();
            } else {
                SUPPRESS_OUTBOUND.set(depth);
            }
        }
    }

    public void subscribe(Listener<T> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(Listener<T> listener) {
        listeners.remove(listener);
    }
}
