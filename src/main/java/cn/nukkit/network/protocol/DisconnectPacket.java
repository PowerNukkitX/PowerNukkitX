package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.DisconnectFailReason;
import lombok.ToString;

/**
 * @since 15-10-12
 */
@ToString
public class DisconnectPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public DisconnectFailReason reason = DisconnectFailReason.UNKNOWN;
    public boolean hideDisconnectionScreen = false;
    public String message;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.reason = DisconnectFailReason.values()[this.getVarInt()];
        this.hideDisconnectionScreen = this.getBoolean();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.reason.ordinal());
        this.putBoolean(this.hideDisconnectionScreen);
        if (!this.hideDisconnectionScreen) {
            this.putString(this.message);
        }
    }

}
