package cn.nukkit.network.connection;

import cn.nukkit.network.protocol.DisconnectPacket;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BedrockServerSession extends BedrockSession {

    public BedrockServerSession(BedrockPeer peer, int subClientId) {
        super(peer, subClientId);
    }

    public void disconnect(@Nullable String reason, boolean hideReason) {
        this.checkForClosed();

        DisconnectPacket packet = new DisconnectPacket();
        if (reason == null || hideReason) {
            packet.hideDisconnectionScreen = true;
            reason = BedrockDisconnectReasons.DISCONNECTED;
        }
        packet.message = reason;
        this.sendPacketImmediately(packet);
    }
}
