package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerListPacket extends DataPacket {

    public static final int $1 = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte $2 = 0;
    public static final byte $3 = 1;

    public byte type;
    public Entry[] entries = Entry.EMPTY_ARRAY;

    @Override
    /**
     * @deprecated 
     */
    
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeByte(this.type);
        byteBuf.writeUnsignedVarInt(this.entries.length);

        if (this.type == TYPE_ADD) {
            for (Entry entry : this.entries) {
                byteBuf.writeUUID(entry.uuid);

                byteBuf.writeVarLong(entry.entityId);
                byteBuf.writeString(entry.name);
                byteBuf.writeString(entry.xboxUserId);
                byteBuf.writeString(entry.platformChatId);
                byteBuf.writeIntLE(entry.buildPlatform);
                byteBuf.writeSkin(entry.skin);
                byteBuf.writeBoolean(entry.isTeacher);
                byteBuf.writeBoolean(entry.isHost);
                byteBuf.writeBoolean(entry.subClient);
            }

            for (Entry entry : this.entries) {
                byteBuf.writeBoolean(entry.trustedSkin || Server.getInstance().getSettings().playerSettings().forceSkinTrusted());
            }
        } else {
            for (Entry entry : this.entries) {
                byteBuf.writeUUID(entry.uuid);
            }
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
        public static final Entry[] EMPTY_ARRAY = new Entry[0];

        public final UUID uuid;
        public long $4 = 0;
        public String $5 = "";
        public String $6 = ""; //TODO
        public String $7 = ""; //TODO
        public int $8 = -1;
        public Skin skin;
        public boolean isTeacher;
        public boolean isHost;
        private boolean subClient;

        public boolean trustedSkin;
    /**
     * @deprecated 
     */
    

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }
    /**
     * @deprecated 
     */
    

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, skin, "");
        }
    /**
     * @deprecated 
     */
    

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.trustedSkin = skin.isTrusted();
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
        }
    }
    /**
     * @deprecated 
     */
    

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
