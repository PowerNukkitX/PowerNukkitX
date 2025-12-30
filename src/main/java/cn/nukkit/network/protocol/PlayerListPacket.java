package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

import java.awt.*;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerListPacket extends DataPacket {
    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = Entry.EMPTY_ARRAY;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
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
                byteBuf.writeIntLE(entry.color.getRGB());
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
        private boolean subClient;
        public boolean trustedSkin;
        public Color color = Color.WHITE;

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this(uuid, entityId, name, skin, "", Color.WHITE);
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, Color color) {
            this(uuid, entityId, name, skin, "", color);
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxUserId, Color color) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.trustedSkin = skin.isTrusted();
            this.xboxUserId = xboxUserId == null ? "" : xboxUserId;
            this.color = color;
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_LIST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
