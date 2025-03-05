package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.UUIDValidator;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePackClientResponsePacket extends DataPacket {
    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    public byte responseStatus;
    public Entry[] packEntries;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.responseStatus = (byte) byteBuf.readByte();
        this.packEntries = new Entry[byteBuf.readShortLE()];
        for (int i = 0; i < this.packEntries.length; i++) {
            String[] entry = byteBuf.readString().split("_");

            if(UUIDValidator.isValidUUID(entry[0])) {
                // Literally a server crash if spammed.
                // @Zwuiix
                this.packEntries[i] = new Entry(UUID.fromString(entry[0]), entry[1]);
            }
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeByte(this.responseStatus);
        byteBuf.writeShortLE(this.packEntries.length);
        for (Entry entry : this.packEntries) {
            byteBuf.writeString(entry.uuid.toString() + '_' + entry.version);
        }
    }

    @ToString
    public static class Entry {
        public final UUID uuid;
        public final String version;

        public Entry(UUID uuid, String version) {
            this.uuid = uuid;
            this.version = version;
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
