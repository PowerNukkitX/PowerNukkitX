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
            return PacketSignal.HANDLED;
        }

        final Player player = this.session.getPlayer();
        final PacketHandler packetHandler = PacketHandlerRegistry.getPacketHandler(packet.getClass());

        if (packetHandler != null && !packetHandler.runsOnNetworkThread() && player != null && player.spawned) {
            this.session.getPlayerHandle().handlePacket(packet);
            return PacketSignal.HANDLED;
        }

        if (player != null && firePacketReceive(player, packet)) {
            return PacketSignal.UNHANDLED;
        }
        if (packetHandler != null) {
            if (player != null && firePacketHandle(player, packet)) {
                return PacketSignal.UNHANDLED;
            }
            packetHandler.handle(packet, this.session, this.server);
            return PacketSignal.HANDLED;
        }
        return BedrockPacketHandler.super.handlePacket(packet);
    }

    /**
     * Dispatches a queued inbound packet on the main thread: fires the receive/handle events then
     * runs the handler. Installed on the player via {@link Player#setInboundProcessor}.
     */
    void processInbound(BedrockPacket packet) {
        final Player player = this.session.getPlayer();
        if (player == null || firePacketReceive(player, packet)) {
            return;
        }
        final PacketHandler packetHandler = PacketHandlerRegistry.getPacketHandler(packet.getClass());
        if (packetHandler == null || firePacketHandle(player, packet)) {
            return;
        }
        packetHandler.handle(packet, this.session, this.server);
    }

    private boolean firePacketReceive(Player player, BedrockPacket packet) {
        if (PacketReceiveEvent.getHandlers().isEmpty()) {
            return false;
        }
        final PacketReceiveEvent event = new PacketReceiveEvent(player, packet);
        this.server.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    private boolean firePacketHandle(Player player, BedrockPacket packet) {
        if (PacketHandleEvent.getHandlers().isEmpty()) {
            return false;
        }
        final PacketHandleEvent event = new PacketHandleEvent(player, packet);
        this.server.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    // client closes the connection
    @Override
    public void onDisconnect(String reason) {
        final Player player = this.session.getPlayer();
        if (player != null) {
            if (player.spawned) {
                player.requestClose(reason);
            } else {
                player.close(reason);
            }
        }
        BedrockPacketHandler.super.onDisconnect(reason);
    }
}