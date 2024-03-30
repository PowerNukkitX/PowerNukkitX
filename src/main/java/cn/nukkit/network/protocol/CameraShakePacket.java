package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CameraShakePacket extends DataPacket {
    public float intensity;
    public float duration;
    public CameraShakeType shakeType;
    public CameraShakeAction shakeAction;

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_SHAKE_PACKET;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.intensity = byteBuf.readFloatLE();
        this.duration = byteBuf.readFloatLE();
        this.shakeType = CameraShakeType.values()[byteBuf.readByte()];
        this.shakeAction = CameraShakeAction.values()[byteBuf.readByte()];
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        
        byteBuf.writeFloatLE(this.intensity);
        byteBuf.writeFloatLE(this.duration);
        byteBuf.writeByte((byte) this.shakeType.ordinal());
        byteBuf.writeByte((byte) this.shakeAction.ordinal());
    }

    public enum CameraShakeAction {
        ADD,
        STOP
    }

    public enum CameraShakeType {
        POSITIONAL,
        ROTATIONAL
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
