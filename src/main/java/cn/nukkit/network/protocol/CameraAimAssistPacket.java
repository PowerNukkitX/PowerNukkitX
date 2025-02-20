package cn.nukkit.network.protocol;

import cn.nukkit.math.Vector2f;
import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;
import org.checkerframework.framework.qual.Unused;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CameraAimAssistPacket extends DataPacket {
    private String presetId;
    private Vector2f viewAngle;
    private float distance;
    private TargetMode targetMode;
    private Action action;

    @Override
    public int pid() {
        return ProtocolInfo.CAMERA_AIM_ASSIST_PACKET;
    }

    @SuppressWarnings("unused")
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.setPresetId(byteBuf.readString());
        this.setViewAngle(byteBuf.readVector2f());
        this.setDistance(byteBuf.readFloatLE());
        this.setTargetMode(CameraAimAssistPacket.TargetMode.values()[byteBuf.readUnsignedByte()]);
        this.setAction(CameraAimAssistPacket.Action.values()[byteBuf.readUnsignedByte()]);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.presetId);
        byteBuf.writeVector2f(this.getViewAngle());
        byteBuf.writeFloatLE(this.getDistance());
        byteBuf.writeByte(this.getTargetMode().ordinal());
        byteBuf.writeByte(this.getAction().ordinal());
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public enum TargetMode {
        ANGLE,
        DISTANCE,
        COUNT
    }

    public enum Action {
        SET,
        CLEAR,
        COUNT
    }
}
