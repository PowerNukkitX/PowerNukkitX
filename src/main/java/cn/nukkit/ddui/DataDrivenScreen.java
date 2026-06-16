package cn.nukkit.ddui;

import cn.nukkit.Player;
import cn.nukkit.ddui.element.LayoutElement;
import cn.nukkit.ddui.properties.DataDrivenProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import org.cloudburstmc.protocol.bedrock.data.ddui.DataStoreChange;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundDataDrivenUICloseScreenPacket;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundDataDrivenUIShowScreenPacket;
import org.cloudburstmc.protocol.bedrock.packet.ClientboundDataStorePacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base for all data-driven UI screens.
 *
 * @author xRookieFight
 * @since 06/03/2026
 */
public abstract class DataDrivenScreen extends ObjectProperty<Object> {

    private static final AtomicInteger FORM_ID_COUNTER = new AtomicInteger(0);

    private static final Map<Player, Map<Integer, DataDrivenScreen>> PLAYER_DDUI_SCREENS =
            new ConcurrentHashMap<>();

    public abstract String getIdentifier();

    public abstract String getProperty();

    /**
     * Returns the per-player property name, with formId suffix
     */
    public String getClientProperty(Player player) {
        Integer formId = playerFormIds.get(player);
        if (formId == null) {
            return getProperty();
        }
        return getProperty() + "_" + formId;
    }

    private final Set<Player> viewers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<Player, Integer> playerFormIds = new ConcurrentHashMap<>();

    protected final LayoutElement layout;

    protected DataDrivenScreen() {
        super("");
        this.layout = new LayoutElement(this);
        this.setProperty(layout);
    }

    public void show(Player player) {
        // Close any existing screen for this player first
        Map<Integer, DataDrivenScreen> existing = PLAYER_DDUI_SCREENS.remove(player);
        if (existing != null) {
            for (Map.Entry<Integer, DataDrivenScreen> entry : existing.entrySet()) {
                DataDrivenScreen old = entry.getValue();
                Integer oldFormId = old.playerFormIds.remove(player);
                old.viewers.remove(player);
                if (oldFormId != null) {
                    final ClientboundDataDrivenUICloseScreenPacket closePacket = new ClientboundDataDrivenUICloseScreenPacket();
                    closePacket.setFormId(oldFormId);
                    player.sendPacket(closePacket);
                }
            }
        }

        int formId = FORM_ID_COUNTER.updateAndGet(v -> v == Integer.MAX_VALUE ? 0 : v + 1);
        String dataStore = getIdentifier().split(":")[0];
        String clientProperty = getProperty() + "_" + formId;

        final DataStoreChange change = new DataStoreChange();
        change.setDataStoreName(dataStore);
        change.setProperty(clientProperty);
        change.setUpdateCount(1);
        change.setTheNewPropertyValue(this.toPropertyValue());

        final ClientboundDataStorePacket data = new ClientboundDataStorePacket();
        data.getUpdates().add(change);

        final ClientboundDataDrivenUIShowScreenPacket show = new ClientboundDataDrivenUIShowScreenPacket();
        show.setScreenId(getIdentifier());
        show.setFormId(formId);
        show.setDataInstanceId(formId);

        player.sendPackets(data, show);

        viewers.add(player);
        playerFormIds.put(player, formId);
        PLAYER_DDUI_SCREENS.computeIfAbsent(player, k -> new ConcurrentHashMap<>()).put(formId, this);
    }

    public void close(Player player) {
        Integer formId = playerFormIds.get(player);
        removeViewer(player);

        final ClientboundDataDrivenUICloseScreenPacket packet = new ClientboundDataDrivenUICloseScreenPacket();
        packet.setFormId(formId != null ? formId : 0);
        player.sendPacket(packet);
    }

    public void removeViewer(Player player) {
        Integer formId = playerFormIds.remove(player);
        viewers.remove(player);
        if (formId != null) {
            Map<Integer, DataDrivenScreen> screens = PLAYER_DDUI_SCREENS.get(player);
            if (screens != null) {
                screens.remove(formId);
                if (screens.isEmpty()) {
                    PLAYER_DDUI_SCREENS.remove(player);
                }
            }
        }
    }

    public List<Player> getAllViewers() {
        return new ArrayList<>(viewers);
    }

    public static DataDrivenScreen getActiveScreen(Player player) {
        Map<Integer, DataDrivenScreen> screens = PLAYER_DDUI_SCREENS.get(player);
        if (screens == null || screens.isEmpty()) return null;
        return screens.entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public static DataDrivenScreen getScreenByFormId(Player player, int formId) {
        Map<Integer, DataDrivenScreen> screens = PLAYER_DDUI_SCREENS.get(player);
        return screens != null ? screens.get(formId) : null;
    }

    public static void removeActiveScreen(Player player) {
        DataDrivenScreen screen = getActiveScreen(player);
        if (screen != null) {
            screen.close(player);
        }
    }

    public DataDrivenProperty<?, ?> resolvePath(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }

        DataDrivenProperty<?, ?> current = this;
        int i = 0;
        while (i < path.length()) {
            char c = path.charAt(i);
            if (c == '.') {
                i++;
                continue;
            }
            String token;
            if (c == '[') {
                int end = path.indexOf(']', i + 1);
                if (end < 0) {
                    return null;
                }
                token = path.substring(i + 1, end);
                i = end + 1;
            } else {
                int end = i;
                while (end < path.length() && path.charAt(end) != '.' && path.charAt(end) != '[') {
                    end++;
                }
                token = path.substring(i, end);
                i = end;
            }

            if (!(current instanceof ObjectProperty<?> obj)) {
                return null;
            }
            current = obj.getProperty(token);
            if (current == null) {
                return null;
            }
        }

        return current;
    }

    @Override
    public DataDrivenScreen getRootScreen() {
        return this;
    }
}
