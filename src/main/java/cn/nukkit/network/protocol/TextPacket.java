package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
import io.netty.util.internal.EmptyArrays;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.*;

/**
 * @since 15-10-13
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TextPacket extends DataPacket {
    public static final byte TYPE_RAW = 0;
    public static final byte TYPE_CHAT = 1;
    public static final byte TYPE_TRANSLATION = 2;
    public static final byte TYPE_POPUP = 3;
    public static final byte TYPE_JUKEBOX_POPUP = 4;
    public static final byte TYPE_TIP = 5;
    public static final byte TYPE_SYSTEM = 6;
    public static final byte TYPE_WHISPER = 7;
    public static final byte TYPE_ANNOUNCEMENT = 8;
    public static final byte TYPE_OBJECT = 9;
    public static final byte TYPE_OBJECT_WHISPER = 10;

    public byte type;
    public String source = "";
    public String message = "";
    public String[] parameters = EmptyArrays.EMPTY_STRINGS;
    public boolean isLocalized = false;
    public String xboxUserId = "";
    public String platformChatId = "";
    /**
     * @since v685
     */
    public OptionalValue<String> filteredMessage = OptionalValue.of("");
    @Override
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
        //this.filteredMessage = byteBuf.readString(); //TODO: Its optional now
    }

    @Override
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
        byteBuf.writeOptional(this.filteredMessage, (s) -> byteBuf.writeString(s));
    }

    @Override
    public int pid() {
        return ProtocolInfo.TEXT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}