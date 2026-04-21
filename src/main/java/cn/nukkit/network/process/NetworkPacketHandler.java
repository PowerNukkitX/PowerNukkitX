package cn.nukkit.network.process;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.server.PacketHandleEvent;
import cn.nukkit.event.server.PacketReceiveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;

/**
 * @author Kaooot
 */
@Slf4j
@RequiredArgsConstructor
public class NetworkPacketHandler implements BedrockPacketHandler {

    private final Server server;
    private final PlayerSessionHolder session;

    @Override
    public PacketSignal handlePacket(BedrockPacket packet) {
        if (!this.session.checkRateLimits(this.server)) {
            return PacketSignal.UNHANDLED;
        }
        final Player player;
        if ((player = this.session.getPlayer()) != null) {
            final PacketReceiveEvent packetReceiveEvent = new PacketReceiveEvent(player, packet);
            this.server.getPluginManager().callEvent(packetReceiveEvent);
            if (packetReceiveEvent.isCancelled()) {
                return PacketSignal.UNHANDLED;
            }
        }
        final PacketHandler packetHandler;
        if ((packetHandler = PacketHandlerRegistry.getPacketHandler(packet.getClass())) != null) {
            if (player != null) {
                final PacketHandleEvent event = new PacketHandleEvent(player, packet);
                this.server.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return PacketSignal.UNHANDLED;
                }
            }
            packetHandler.handle(packet, this.session, this.server);
            return PacketSignal.HANDLED;
        }
        return BedrockPacketHandler.super.handlePacket(packet);
    }

    @Override
    public void onDisconnect(String reason) {
        if (this.session.getPlayer() != null) {
            this.session.getPlayer().close(reason);
        }
        BedrockPacketHandler.super.onDisconnect(reason);
    }
}