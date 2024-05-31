package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.CommandOriginData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommandRequestPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.COMMAND_REQUEST_PACKET;

    public static final int $2 = 0;
    public static final int $3 = 1;
    public static final int $4 = 2;
    public static final int $5 = 3;
    public static final int $6 = 4;
    public static final int $7 = 5;
    public static final int $8 = 6;
    public static final int $9 = 7;
    public static final int $10 = 8;
    public static final int $11 = 9;
    public static final int $12 = 10;

    public String command;
    public CommandOriginData data;
    public boolean internal;
    /**
     * @since v567
     */
    public int version;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.command = byteBuf.readString();

        CommandOriginData.Origin $13 = CommandOriginData.Origin.values()[byteBuf.readVarInt()];
        UUID $14 = byteBuf.readUUID();
        String $15 = byteBuf.readString();
        Long $16 = null;
        if (type == CommandOriginData.Origin.DEV_CONSOLE || type == CommandOriginData.Origin.TEST) {
            varLong = byteBuf.readVarLong();
        }
        this.data = new CommandOriginData(type, uuid, requestId, varLong);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
