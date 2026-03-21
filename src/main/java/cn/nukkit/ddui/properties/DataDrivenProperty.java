package cn.nukkit.ddui.properties;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.ddui.DataStorePropertyType;
import cn.nukkit.ddui.DataDrivenScreen;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Abstract base for all data-driven UI properties.
 *
 * @param <T> The stored value type (e.g. String, Boolean, Long, Map).
 * @param <K> The listener callback data type. Use the same type as T when there
 *            is no distinction (e.g. StringProperty uses String for both).
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public abstract class DataDrivenProperty<T, K> {

    public abstract DataStorePropertyType getType();

    @Getter
    protected final ObjectProperty parent;

    private final Set<BiConsumer<Player, Object>> listeners = new LinkedHashSet<>();

    @Getter
    protected long triggerCount = 0;

    @Getter
    @Setter
    protected String name;

    @Getter
    @Setter
    protected T value;

    protected DataDrivenProperty(String name, T value, ObjectProperty parent) {
        this.name   = name;
        this.value  = value;
        this.parent = parent;
    }

    public void addListener(BiConsumer<Player, Object> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(BiConsumer<Player, Object> listener) {
        this.listeners.remove(listener);
    }

    /**
     * Fire all registered listeners and increment the trigger counter.
     *
     * @param player The player that triggered the event.
     * @param data   Event payload (K or T depending on context).
     */
    public void triggerListeners(Player player, Object data) {
        this.triggerCount++;
        for (BiConsumer<Player, Object> listener : this.listeners) {
            listener.accept(player, data);
        }
    }

    /**
     * Builds the dotted/bracket path of this property relative to the root,
     * e.g. {@code layout.0.label} or {@code layout[2].text}.
     */
    public String getPath() {
        if (this.parent == null) {
            return this.name;
        }

        String parentPath = this.parent.getPath();

        if (this.parent.getName().isEmpty()) {
            return this.name;
        }

        try {
            Integer.parseInt(this.name);
            return parentPath + "[" + this.name + "]";
        } catch (NumberFormatException ignored) {
            return parentPath + "." + this.name;
        }
    }

    /**
     * Walks up the parent chain and returns the owning {@link DataDrivenScreen},
     * or {@code null} if this property is not attached to a screen.
     */
    public DataDrivenScreen getRootScreen() {
        if (this.parent != null) {
            return this.parent.getRootScreen();
        }
        return null;
    }
}