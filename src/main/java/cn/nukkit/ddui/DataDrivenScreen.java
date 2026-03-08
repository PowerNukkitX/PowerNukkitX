package cn.nukkit.ddui;

import cn.nukkit.Player;
import cn.nukkit.ddui.element.LayoutElement;
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
    }

    public void close(Player player) {
        viewers.remove(player);

        ClientboundDataDrivenUICloseScreenPacket packet = new ClientboundDataDrivenUICloseScreenPacket();
        packet.setFormId(0);
    }

    public List<Player> getAllViewers() {
        return new ArrayList<>(viewers);
    }

    @Override
    public DataDrivenScreen getRootScreen() {
        return this;
    }
}
