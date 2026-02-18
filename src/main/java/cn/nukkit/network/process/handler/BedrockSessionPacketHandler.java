package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.network.connection.BedrockSession;
import org.cloudburstmc.protocol.bedrock.packet.PacketViolationWarningPacket;
import org.cloudburstmc.protocol.common.PacketSignal;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;

public class BedrockSessionPacketHandler implements BedrockPacketHandler {
    protected final Player player;
    protected final BedrockSession session;
    protected final PlayerHandle handle;

    public BedrockSessionPacketHandler(BedrockSession session) {
        this.player = session.getPlayer();
        this.session = session;
        this.handle = session.getHandle();
    }

    @Override
    public PacketSignal handle(org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket pk) {
        if (player != null) {
            player.close(pk.getKickMessage());
        }
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PacketViolationWarningPacket pk) {
        String playerName = player != null ? player.getName() : "unknown";
        System.out.println("Violation warning from " + playerName + ": causeId=" + pk.getPacketCauseId() + ", type=" + pk.getType() + ", severity=" + pk.getSeverity() + ", context=" + pk.getContext());
        return PacketSignal.HANDLED;
    }
}
