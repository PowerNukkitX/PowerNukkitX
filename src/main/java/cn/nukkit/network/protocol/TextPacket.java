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
    public static final byte TYPE_OBJECT_ANNOUNCEMENT = 11;


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
    public String filteredMessage = "";
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.isLocalized = byteBuf.readBoolean();
        switch (byteBuf.readByte()) {
            case 0 -> {
                for (int i = 0; i < 6; i++) {
                    byteBuf.readString();
                }
                this.type = (byte) byteBuf.readUnsignedByte();
                this.message = byteBuf.readString();
            }
            case 1 -> {
                for (int i = 0; i < 3; i++) {
                    byteBuf.readString();
                }
                this.type = (byte) byteBuf.readUnsignedByte();
                this.source = byteBuf.readString();
                this.message = byteBuf.readString();
            }
            case 2 -> {
                for (int i = 0; i < 3; i++) {
                    byteBuf.readString();
                }
                this.type = (byte) byteBuf.readUnsignedByte();
                this.message = byteBuf.readString();
                this.parameters = byteBuf.readArray(String.class, HandleByteBuf::readString);
            }
        }
        this.xboxUserId = byteBuf.readString();
        this.platformChatId = byteBuf.readString();
        this.filteredMessage = byteBuf.readOptional("", byteBuf::readString);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeBoolean(this.isLocalized || type == TYPE_TRANSLATION);

        switch (this.type) {
            case TYPE_RAW,
                 TYPE_TIP,
                 TYPE_SYSTEM,
                 TYPE_OBJECT,
                 TYPE_OBJECT_WHISPER,
                 TYPE_OBJECT_ANNOUNCEMENT -> {
                byteBuf.writeByte(0);
                byteBuf.writeString("raw");
                byteBuf.writeString("tip");
                byteBuf.writeString("systemMessage");
                byteBuf.writeString("textObjectWhisper");
                byteBuf.writeString("textObjectAnnouncement");
                byteBuf.writeString("textObject");
                byteBuf.writeByte(type);
                byteBuf.writeString(this.message);
            }
            case TYPE_CHAT,
                 TYPE_WHISPER,
                 TYPE_ANNOUNCEMENT -> {
                byteBuf.writeByte(1);
                byteBuf.writeString("chat");
                byteBuf.writeString("whisper");
                byteBuf.writeString("announcement");
                byteBuf.writeByte(type);
                byteBuf.writeString(this.source);
                byteBuf.writeString(this.source);
            }

            case TYPE_TRANSLATION,
                 TYPE_POPUP,
                 TYPE_JUKEBOX_POPUP -> {
                byteBuf.writeByte(2);
                byteBuf.writeString("translate");
                byteBuf.writeString("popup");
                byteBuf.writeString("jukeboxPopup");
                byteBuf.writeByte(type);
                byteBuf.writeString(this.message);
                byteBuf.writeArray(this.parameters, byteBuf::writeString);
            }
        }
        byteBuf.writeString(this.xboxUserId);
        byteBuf.writeString(this.platformChatId);
        byteBuf.writeOptional(OptionalValue.of(filteredMessage), byteBuf::writeString);
    }

    @Override
    public int pid() {
        return ProtocolInfo.TEXT_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
