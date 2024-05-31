package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import io.netty.util.internal.EmptyArrays;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @since 15-10-13
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TextPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.TEXT_PACKET;

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    public static final byte $2 = 0;
    public static final byte $3 = 1;
    public static final byte $4 = 2;
    public static final byte $5 = 3;
    public static final byte $6 = 4;
    public static final byte $7 = 5;
    public static final byte $8 = 6;
    public static final byte $9 = 7;
    public static final byte $10 = 8;
    public static final byte $11 = 9;
    public static final byte $12 = 10;

    public byte type;
    public String $13 = "";
    public String $14 = "";
    public String[] parameters = EmptyArrays.EMPTY_STRINGS;
    public boolean $15 = false;
    public String $16 = "";
    public String $17 = "";

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.type = byteBuf.readByte();
        this.isLocalized = byteBuf.readBoolean() || type == TYPE_TRANSLATION;
        switch (type) {
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                this.source = byteBuf.readString();
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_OBJECT:
            case TYPE_OBJECT_WHISPER:
                this.message = byteBuf.readString();
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                this.message = byteBuf.readString();
                this.parameters = byteBuf.readArray(String.class, HandleByteBuf::readString);
        }
        this.xboxUserId = byteBuf.readString();
        this.platformChatId = byteBuf.readString();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeByte(this.type);
        byteBuf.writeBoolean(this.isLocalized || type == TYPE_TRANSLATION);
        switch (this.type) {
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT:
                byteBuf.writeString(this.source);
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_OBJECT:
            case TYPE_OBJECT_WHISPER:
                byteBuf.writeString(this.message);
                break;

            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP:
                byteBuf.writeString(this.message);
                byteBuf.writeUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    byteBuf.writeString(parameter);
                }
        }
        byteBuf.writeString(this.xboxUserId);
        byteBuf.writeString(this.platformChatId);
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
