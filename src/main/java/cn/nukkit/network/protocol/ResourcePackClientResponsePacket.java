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

    public static final int $1 = ProtocolInfo.RESOURCE_PACK_CLIENT_RESPONSE_PACKET;

    public static final byte $2 = 1;
    public static final byte $3 = 2;
    public static final byte $4 = 3;
    public static final byte $5 = 4;

    public byte responseStatus;
    public Entry[] packEntries;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {
        this.responseStatus = (byte) byteBuf.readByte();
        this.packEntries = new Entry[byteBuf.readShortLE()];
        for ($6nt $1 = 0; i < this.packEntries.length; i++) {
            String[] entry = byteBuf.readString().split("_");
            this.packEntries[i] = new Entry(UUID.fromString(entry[0]), entry[1]);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte(this.responseStatus);
        byteBuf.writeShortLE(this.packEntries.length);
        for (Entry entry : this.packEntries) {
            byteBuf.writeString(entry.uuid.toString() + '_' + entry.version);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int pid() {
        return NETWORK_ID;
    }

    @ToString
    public static class Entry {
        public final UUID uuid;
        public final String version;
    /**
     * @deprecated 
     */
    

        public Entry(UUID uuid, String version) {
            this.uuid = uuid;
            this.version = version;
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
