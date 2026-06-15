package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.ddui.DataDrivenScreen;
import cn.nukkit.ddui.Observable;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.DataDrivenProperty;
import cn.nukkit.ddui.properties.LongProperty;
import cn.nukkit.ddui.properties.StringProperty;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
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
            if (!screen.getProperty().equals(update.getProperty())) return;

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