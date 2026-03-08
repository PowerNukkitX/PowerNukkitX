package cn.nukkit.ddui;

import cn.nukkit.Player;
import cn.nukkit.ddui.element.LayoutElement;
import cn.nukkit.ddui.properties.DataDrivenProperty;
import cn.nukkit.ddui.properties.ObjectProperty;
import cn.nukkit.network.protocol.ClientboundDataDrivenUICloseScreenPacket;
import cn.nukkit.network.protocol.ClientboundDataDrivenUIShowScreenPacket;
import cn.nukkit.network.protocol.ClientboundDataStorePacket;
import cn.nukkit.network.protocol.types.ddui.DataStoreChange;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Abstract base for all data-driven UI screens.
 * @author xRookieFight
 * @since 06/03/2026
 */
public abstract class DataDrivenScreen extends ObjectProperty<Object> {

    private static final java.util.Map<Player, DataDrivenScreen> ACTIVE_SCREENS =
            new java.util.concurrent.ConcurrentHashMap<>();

    public abstract String getIdentifier();

    public abstract String getProperty();

    private final Set<Player> viewers = new LinkedHashSet<>();

    protected final LayoutElement layout;

    protected DataDrivenScreen() {
        super("");
        this.layout = new LayoutElement(this);
        this.setProperty(layout);
    }

    public void show(Player player) {
        String dataStore = getIdentifier().split(":")[0];
        DataStoreChange change = new DataStoreChange(
                dataStore, getProperty(), 1,
                toPropertyValue());

        ClientboundDataStorePacket data = new ClientboundDataStorePacket();
        data.setUpdates(List.of(change));

        ClientboundDataDrivenUIShowScreenPacket show = new ClientboundDataDrivenUIShowScreenPacket();
        show.setScreenId(getIdentifier());
        show.setFormId(0);
        player.sendDataPackets(data, show);

        viewers.add(player);
        ACTIVE_SCREENS.put(player, this);
    }

    public void close(Player player) {
        viewers.remove(player);
        ACTIVE_SCREENS.remove(player);

        ClientboundDataDrivenUICloseScreenPacket packet = new ClientboundDataDrivenUICloseScreenPacket();
        packet.setFormId(0);
        player.dataPacket(packet);
    }

    public List<Player> getAllViewers() {
        return new ArrayList<>(viewers);
    }

    public static DataDrivenScreen getActiveScreen(Player player) {
        return ACTIVE_SCREENS.get(player);
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
