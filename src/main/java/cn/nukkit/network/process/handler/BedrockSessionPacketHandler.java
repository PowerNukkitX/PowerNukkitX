package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.ddui.DataDrivenScreen;
import cn.nukkit.ddui.properties.BooleanProperty;
import cn.nukkit.ddui.properties.DataDrivenProperty;
import cn.nukkit.ddui.properties.LongProperty;
import cn.nukkit.ddui.properties.StringProperty;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.PacketHandler;
import cn.nukkit.network.protocol.PacketViolationWarningPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.ServerboundDataStorePacket;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class BedrockSessionPacketHandler implements PacketHandler {
    protected final Player player;
    protected final BedrockSession session;
    protected final PlayerHandle handle;

    public BedrockSessionPacketHandler(BedrockSession session) {
        this.player = session.getPlayer();
        this.session = session;
        this.handle = session.getHandle();
    }

    public void handle(DisconnectPacket pk) {
        if (player != null) {
            player.close(pk.message);
        }
    }

    public void handle(PacketViolationWarningPacket pk) {
        Optional<String> packetName = Arrays.stream(ProtocolInfo.class.getDeclaredFields())
                .filter(field -> field.getType() == Byte.TYPE)
                .filter(field -> {
                    try {
                        return field.getByte(null) == pk.packetId;
                    } catch (IllegalAccessException e) {
                        return false;
                    }
                }).map(Field::getName).findFirst();
        System.out.println("Violation warning from " + player.getName() + ": " + packetName.map(name -> " for packet " + name).orElse("") + ": " + pk);
    }

    @Override
    public void handle(ServerboundDataStorePacket pk) {
        if (player == null) return;

        var update = pk.getUpdate();
        if (update == null) return;

        DataDrivenScreen screen = DataDrivenScreen.getActiveScreen(player);
        if (screen == null) return;

        String dataStore = screen.getIdentifier().split(":")[0];
        if (!dataStore.equals(update.getDataStoreName())) return;
        if (!screen.getProperty().equals(update.getProperty())) return;

        DataDrivenProperty<?, ?> property = screen.resolvePath(update.getPath());
        if (property == null) return;

        Object data = update.getData();
        if (property instanceof LongProperty) {
            if (data instanceof Number n) {
                property.triggerListeners(player, n.longValue());
            } else {
                property.triggerListeners(player, 0L);
            }
            return;
        }
        if (property instanceof BooleanProperty) {
            if (data instanceof Boolean b) {
                property.triggerListeners(player, b);
            }
            return;
        }
        if (property instanceof StringProperty) {
            if (data instanceof String s) {
                property.triggerListeners(player, s);
            }
            return;
        }

        property.triggerListeners(player, data);
    }
}
