package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PlayerVideoCapturePacket extends DataPacket {

    public Action captureAction;
    public boolean action;
    public int frameRate;
    public String filePrefix;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        if (byteBuf.readableBytes() > 1) {
            this.captureAction = Action.START_VIDEO_CAPTURE;
            this.action = byteBuf.readBoolean();
            this.frameRate = byteBuf.readIntLE();
            this.filePrefix = byteBuf.readString();
        } else if (byteBuf.readableBytes() == 1) {
            this.captureAction = Action.STOP_VIDEO_CAPTURE;
            this.action = byteBuf.readBoolean();
        } else {
            this.captureAction = Action.DEFAULT;
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        if (captureAction.equals(PlayerVideoCapturePacket.Action.START_VIDEO_CAPTURE)) {
            byteBuf.writeBoolean(action);
            byteBuf.writeIntLE(frameRate);
            byteBuf.writeString(filePrefix);
        } else if (captureAction.equals(PlayerVideoCapturePacket.Action.STOP_VIDEO_CAPTURE)) {
            byteBuf.writeBoolean(action);
        }
    }

    @Override
    public int pid() {
        return ProtocolInfo.PLAYER_VIDEO_CAPTURE_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }

    public enum Action {
        START_VIDEO_CAPTURE,
        STOP_VIDEO_CAPTURE,
        DEFAULT
    }
}
