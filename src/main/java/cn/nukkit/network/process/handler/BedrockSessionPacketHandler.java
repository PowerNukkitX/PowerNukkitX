package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.connection.BedrockSession;
import cn.nukkit.network.protocol.DisconnectPacket;
import cn.nukkit.network.protocol.PacketHandler;
import cn.nukkit.network.protocol.PacketViolationWarningPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

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
}
