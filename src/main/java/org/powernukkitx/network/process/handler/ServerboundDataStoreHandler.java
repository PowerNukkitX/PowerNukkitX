package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.Server;
import org.powernukkitx.ddui.DataDrivenScreen;
import org.powernukkitx.ddui.Observable;
import org.powernukkitx.ddui.properties.BooleanProperty;
import org.powernukkitx.ddui.properties.DataDrivenProperty;
import org.powernukkitx.ddui.properties.LongProperty;
import org.powernukkitx.ddui.properties.StringProperty;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDataStorePacket;

/**
 * @author Kaooot
 */
public class ServerboundDataStoreHandler implements PacketHandler<ServerboundDataStorePacket> {

    @Override
    public void handle(ServerboundDataStorePacket packet, PlayerSessionHolder holder, Server server) {
        Player player = holder.getPlayer();

        Observable.withOutboundSuppressed(() -> {
            var update = packet.getUpdate();
            if (update == null) return;

            DataDrivenScreen screen = DataDrivenScreen.getActiveScreen(player);
            if (screen == null) return;

            String dataStore = screen.getIdentifier().split(":")[0];
            if (!dataStore.equals(update.getDataStoreName())) return;
            if (!screen.getClientProperty(player).equals(update.getProperty())) return;

            DataDrivenProperty<?, ?> property = screen.resolvePath(update.getPath());
            if (property == null) return;

            Object data = update.getData();
            switch (property) {
                case LongProperty ignored when data instanceof Number n -> property.triggerListeners(player, n.longValue());
                case LongProperty ignored -> property.triggerListeners(player, 0L);
                case BooleanProperty ignored when data instanceof Boolean b -> property.triggerListeners(player, b);
                case StringProperty ignored when data instanceof String s -> property.triggerListeners(player, s);
                default -> {}
            }
        });
    }
}