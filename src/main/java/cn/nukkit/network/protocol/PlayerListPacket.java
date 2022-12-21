package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.data.Skin;
import lombok.ToString;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
@ToString
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = Entry.EMPTY_ARRAY;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putUnsignedVarInt(this.entries.length);

        if (this.type == TYPE_ADD) {
            for (Entry entry : this.entries) {
                this.putUUID(entry.uuid);

                this.putVarLong(entry.entityId);
                this.putString(entry.name);
                this.putString(entry.xboxUserId);
                this.putString(entry.platformChatId);
                this.putLInt(entry.buildPlatform);
                this.putSkin(entry.skin);
                this.putBoolean(entry.isTeacher);
                this.putBoolean(entry.isHost);
            }

            for (Entry entry : this.entries) {
                this.putBoolean(entry.trustedSkin);
            }
        } else {
            for (Entry entry : this.entries) {
                this.putUUID(entry.uuid);
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @ToString
    public static class Entry {
        public static final Entry[] EMPTY_ARRAY = new Entry[0];

        public final UUID uuid;
        public long entityId = 0;
        public String name = "";
        public String xboxUserId = ""; //TODO
        public String platformChatId = ""; //TODO
        public int buildPlatform = -1;
        public Skin skin;
        public boolean isTeacher;
        public boolean isHost;
        @PowerNukkitXOnly
        @Since("1.19.50-r3")
        public boolean trustedSkin = Server.getInstance().isForceSkinTrusted();

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, skin, "");
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
        }
    }

}
