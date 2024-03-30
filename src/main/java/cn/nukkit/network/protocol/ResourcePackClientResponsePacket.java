package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePackClientResponsePacket extends DataPacket {

    public static final int NETWORK_ID = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

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
            this.packEntries[i] = new Entry(UUID.fromString(entry[0]), entry[1]);
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

    @Override
    public int pid() {
        return NETWORK_ID;
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

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
