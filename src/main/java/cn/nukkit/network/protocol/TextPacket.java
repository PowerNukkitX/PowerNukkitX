package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
import io.netty.util.internal.EmptyArrays;
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
        boolean needsTranslation = byteBuf.readBoolean();

        int mode = byteBuf.readByte();
        this.type = byteBuf.readByte();

        switch (mode) {
            case 0: // MessageOnly
                this.message = byteBuf.readString();
                break;

            case 1: // AuthorAndMessage
                this.source = byteBuf.readString();
                this.message = byteBuf.readString();
                break;

            case 2: // MessageAndParams
                this.message = byteBuf.readString();
                int len = byteBuf.readUnsignedVarInt();
                this.parameters = new String[len];
                for (int i = 0; i < len; i++) {
                    this.parameters[i] = byteBuf.readString();
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown TextPacket mode: " + mode);
        }

        this.isLocalized = needsTranslation;

        this.xboxUserId = byteBuf.readString();
        this.platformChatId = byteBuf.readString();

        String filtered = byteBuf.readString();
        this.filteredMessage = OptionalValue.of(filtered);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        boolean needsTranslation = this.isLocalized || this.type == TYPE_TRANSLATION;
        byteBuf.writeBoolean(needsTranslation);

        switch (this.type) {

            // MessageOnly
            case TYPE_RAW:
            case TYPE_TIP:
            case TYPE_SYSTEM:
            case TYPE_OBJECT:
            case TYPE_OBJECT_WHISPER: {
                byteBuf.writeByte(0);
                byteBuf.writeByte(this.type);

                String msg = (this.message == null || this.message.isEmpty()) ? " " : this.message;
                byteBuf.writeString(msg);
                break;
            }

            // AuthorAndMessage
            case TYPE_CHAT:
            case TYPE_WHISPER:
            case TYPE_ANNOUNCEMENT: {
                byteBuf.writeByte(1);
                byteBuf.writeByte(this.type);

                byteBuf.writeString(this.source == null ? "" : this.source);

                String msg = (this.message == null || this.message.isEmpty()) ? " " : this.message;
                byteBuf.writeString(msg);
                break;
            }

            // MessageAndParams
            case TYPE_TRANSLATION:
            case TYPE_POPUP:
            case TYPE_JUKEBOX_POPUP: {
                byteBuf.writeByte(2);
                byteBuf.writeByte(this.type);

                String msg = (this.message == null || this.message.isEmpty()) ? " " : this.message;
                byteBuf.writeString(msg);

                byteBuf.writeUnsignedVarInt(this.parameters.length);
                for (String parameter : this.parameters) {
                    byteBuf.writeString(parameter == null ? "" : parameter);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown TextPacket type: " + this.type);
        }

        byteBuf.writeString(this.xboxUserId == null ? "" : this.xboxUserId);
        byteBuf.writeString(this.platformChatId == null ? "" : this.platformChatId);

        String filtered = this.filteredMessage.isPresent() ? this.filteredMessage.get() : "";
        byteBuf.writeString(filtered);
    }


    @Override
    public int pid() {
        return ProtocolInfo.TEXT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}