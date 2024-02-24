package cn.nukkit.network.protocol;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.ToString;

import java.util.UUID;

@ToString
public class PlayerSkinPacket extends DataPacket {

    public UUID uuid;
    public Skin skin;
    public String newSkinName;
    public String oldSkinName;

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_SKIN_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        uuid = byteBuf.readUUID();
        skin = byteBuf.readSkin();
        newSkinName = byteBuf.readString();
        oldSkinName = byteBuf.readString();
        if (byteBuf.isReadable()) { // -facepalm-
            skin.setTrusted(byteBuf.readBoolean());
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUUID(uuid);
        byteBuf.writeSkin(skin);
        byteBuf.writeString(newSkinName);
        byteBuf.writeString(oldSkinName);
        byteBuf.writeBoolean(skin.isTrusted() || Server.getInstance().isForceSkinTrusted());
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
