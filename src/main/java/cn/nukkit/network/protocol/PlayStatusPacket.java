package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-13
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayStatusPacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.PLAY_STATUS_PACKET;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_FAILED_CLIENT = 1;
    public static final int LOGIN_FAILED_SERVER = 2;
    public static final int PLAYER_SPAWN = 3;
    public static final int LOGIN_FAILED_INVALID_TENANT = 4;
    public static final int LOGIN_FAILED_VANILLA_EDU = 5;
    public static final int LOGIN_FAILED_EDU_VANILLA = 6;
    public static final int LOGIN_FAILED_SERVER_FULL = 7;
    public static final int LOGIN_FAILED_EDITOR_TO_VANILLA_MISMATCH = 8;
    public static final int LOGIN_FAILED_VANILLA_TO_EDITOR_MISMATCH = 9;

    public int status;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeInt(this.status);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
