package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.network.protocol.types.camera.aimassist.ClientCameraAimAssistPacketAction;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientCameraAimAssistPacket extends DataPacket {
    private String cameraPresetId;
    private ClientCameraAimAssistPacketAction action;
    private boolean allowAimAssist;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.cameraPresetId = byteBuf.readString();
        this.action = ClientCameraAimAssistPacketAction.values()[byteBuf.readByte()];
        this.allowAimAssist = byteBuf.readBoolean();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(cameraPresetId);
        byteBuf.writeByte(action.ordinal());
        byteBuf.writeBoolean(allowAimAssist);
    }

    @Override
    public int pid() {
        return ProtocolInfo.CLIENT_CAMERA_AIM_ASSIST_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
