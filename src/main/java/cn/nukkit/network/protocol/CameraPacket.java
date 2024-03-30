package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CameraPacket extends DataPacket {

    public long cameraUniqueId;
    public long playerUniqueId;

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.cameraUniqueId = byteBuf.readVarLong();
        this.playerUniqueId = byteBuf.readVarLong();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

        byteBuf.writeEntityUniqueId(this.cameraUniqueId);
        byteBuf.writeEntityUniqueId(this.playerUniqueId);
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
